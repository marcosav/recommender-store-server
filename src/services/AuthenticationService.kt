package com.gmail.marcosav2010.services

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.model.*
import com.gmail.marcosav2010.routes.UnauthorizedException
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.util.pipeline.*
import org.kodein.di.DI
import org.kodein.di.instance
import java.time.Clock
import java.util.*

class AuthenticationService {

    private val clock = Clock.systemUTC()

    private val jwtAlgorithm = Algorithm.HMAC384(System.getenv(Constants.JWT_SECRET_ENV))

    val jwtVerifier: JWTVerifier =
        (JWT.require(jwtAlgorithm) as JWTVerifier.BaseVerification).build { Date(clock.millis()) }

    fun token(subject: String, cart: SessionCart? = null, userId: Long? = null, username: String? = null): String =
        JWT.create()
            .withSubject(subject)
            .apply { userId?.let { withClaim(Constants.USER_ID_CLAIM, it) } }
            .apply { username?.let { withClaim(Constants.USERNAME_CLAIM, it) } }
            .apply { cart?.let { withClaim(Constants.CART_CLAIM, it.toJSON()) } }
            .withExpiresAt(Date.from(clock.instant().plusSeconds(Constants.SESSION_DURATION)))
            .sign(jwtAlgorithm)
}

inline val PipelineContext<*, ApplicationCall>.safeSession: Session? get() = call.authentication.principal()
inline val PipelineContext<*, ApplicationCall>.session: Session get() = safeSession!!

fun PipelineContext<*, ApplicationCall>.assertIdentified() = session.userId ?: throw UnauthorizedException()

fun Authentication.Configuration.setupJWT(di: DI) {
    jwt {
        val authenticationService by di.instance<AuthenticationService>()
        val roleService by di.instance<RoleService>()

        verifier(authenticationService.jwtVerifier)
        realm = "mav"

        validate {
            val sessionId = it.payload.subject
            val userId = it.payload.claims[Constants.USER_ID_CLAIM]?.asLong()
            val username = it.payload.claims[Constants.USERNAME_CLAIM]?.asString()

            if (userId != null)
                roleService.findForUser(userId)?.let { r -> Session(sessionId, userId, username, r) }
            else {
                val cart = it.payload.getClaim(Constants.CART_CLAIM).runCatching { asList(CartProduct::class.java) }
                    .getOrNull()
                Session(sessionId, null, null, Role.ANONYMOUS, cart)
            }
        }
    }
}