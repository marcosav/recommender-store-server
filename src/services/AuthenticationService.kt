package com.gmail.marcosav2010.services

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.model.CartProduct
import com.gmail.marcosav2010.model.Role
import com.gmail.marcosav2010.model.Session
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

    private val jwtAlgorithm = Algorithm.HMAC384(System.getenv("JWT_SECRET"))

    val jwtVerifier: JWTVerifier =
        (JWT.require(jwtAlgorithm) as JWTVerifier.BaseVerification).build { Date(clock.millis()) }

    fun authenticate(subject: String): String =
        JWT.create()
            .withSubject(subject)
            .withExpiresAt(Date.from(clock.instant().plusSeconds(Constants.SESSION_DURATION)))
            .sign(jwtAlgorithm)
}

inline val PipelineContext<*, ApplicationCall>.safeSession: Session? get() = call.authentication.principal()
inline val PipelineContext<*, ApplicationCall>.session: Session get() = safeSession!!

fun Authentication.Configuration.setupJWT(di: DI) {
    jwt {
        val authenticationService by di.instance<AuthenticationService>()
        val roleService by di.instance<RoleService>()

        verifier(authenticationService.jwtVerifier)
        realm = "mav"

        validate {
            val sessionId = UUID.fromString(it.payload.subject)
            val userId = it.payload.getClaim("uid").asString().toLongOrNull()
            val cart = it.payload.getClaim("cart").asList(CartProduct::class.java)

            if (userId != null)
                roleService.findForUser(userId)?.let { r -> Session(sessionId, userId, r, cart) }
            else
                Session(sessionId, null, Role.ANONYMOUS, cart)
        }
    }
}