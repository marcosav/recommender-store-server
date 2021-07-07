package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.model.PublicUser
import com.gmail.marcosav2010.services.AuthenticationService
import com.gmail.marcosav2010.services.RoleService
import com.gmail.marcosav2010.services.UserService
import com.gmail.marcosav2010.services.cart.CartService
import com.gmail.marcosav2010.services.session
import com.gmail.marcosav2010.utils.BCryptEncoder
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.locations.post
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

@KtorExperimentalLocationsAPI
fun Route.login() {

    val authenticationService by closestDI().instance<AuthenticationService>()
    val userService by closestDI().instance<UserService>()
    val roleService by closestDI().instance<RoleService>()
    val cartService by closestDI().instance<CartService>()

    post<Login> {
        session.userId?.let { return@post call.respond(HttpStatusCode.NoContent) }

        if (it.username.isEmpty() || it.password.isEmpty())
            throw UnauthorizedException()

        val matched = userService.findByUsername(it.username)
        if (matched != null) {
            if (!BCryptEncoder.verify(it.password, matched.password))
                throw UnauthorizedException()

            val role = roleService.findForUser(matched.id!!)

            val cart = session.cart.let { c -> cartService.mergeCarts(matched.id, c) }

            call.respond(
                LoginResponse(
                    authenticationService.token(session.sessionId, cart, matched.id, matched.nickname, role),
                    matched.toPublicUser()
                )
            )

        } else throw UnauthorizedException()
    }
}

data class LoginResponse(val token: String, val user: PublicUser)

@KtorExperimentalLocationsAPI
@Location("/login")
data class Login(val username: String, val password: String)