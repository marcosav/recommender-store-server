package com.gmail.marcosav2010.services

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.model.*
import com.gmail.marcosav2010.routes.ForbiddenException
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

    fun token(session: Session) = token(
        session.sessionId,
        session.cart,
        session.userId,
        session.username,
        session.role
    )

    fun token(
        subject: String,
        cart: SessionCart,
        userId: Long? = null,
        username: String? = null,
        role: Role? = null
    ): String =
        JWT.create()
            .withSubject(subject)
            .apply { userId?.let { withClaim(Constants.USER_ID_CLAIM, it) } }
            .apply { username?.let { withClaim(Constants.USERNAME_CLAIM, it) } }
            .apply { if (role == Role.ADMIN) withClaim(Constants.ROLE_CLAIM, true) }
            .withClaim(Constants.CART_CLAIM, cart.toJSON())
            .withExpiresAt(Date.from(clock.instant().plusSeconds(Constants.SESSION_DURATION)))
            .sign(jwtAlgorithm)
}

inline val PipelineContext<*, ApplicationCall>.safeSession: Session? get() = call.authentication.principal()
inline val PipelineContext<*, ApplicationCall>.session: Session get() = safeSession!!

fun PipelineContext<*, ApplicationCall>.assertIdentified() = session.userId ?: throw UnauthorizedException()
fun PipelineContext<*, ApplicationCall>.assertAdmin() {
    if (!session.isAdmin) throw ForbiddenException()
}

fun Authentication.Configuration.setupJWT(di: DI) {
    jwt {
        val authenticationService by di.instance<AuthenticationService>()
        val roleService by di.instance<RoleService>()

        verifier(authenticationService.jwtVerifier)
        realm = "mav"

        validate {
            val sessionId = it.payload.subject
            val cart = fromJSON(it.payload.getClaim(Constants.CART_CLAIM).asString())
            val userId = it.payload.claims[Constants.USER_ID_CLAIM]?.asLong()
            val username = it.payload.claims[Constants.USERNAME_CLAIM]?.asString()

            if (userId != null) {
                val role = if (it.payload.claims[Constants.ROLE_CLAIM]?.asBoolean() == true)
                    roleService.findForUser(userId) ?: throw Exception("Unknown user")
                else
                    Role.MEMBER
                Session(sessionId, userId, username, role, cart)
            } else
                Session(sessionId, null, null, Role.ANONYMOUS, cart)
        }
    }
}