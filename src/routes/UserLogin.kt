package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.services.AuthenticationService
import com.gmail.marcosav2010.services.RoleService
import com.gmail.marcosav2010.services.UserService
import com.gmail.marcosav2010.services.cart.CartService
import com.gmail.marcosav2010.services.session
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.di

@KtorExperimentalLocationsAPI
fun Route.login() {

    val authenticationService by di().instance<AuthenticationService>()
    val userService by di().instance<UserService>()
    val roleService by di().instance<RoleService>()
    val cartService by di().instance<CartService>()

    post<Login> {
        if (it.username.isEmpty() || it.password.isEmpty())
            throw UnauthorizedException()

        val matched = userService.findByUsernameAndPassword(it.username, it.password)
        if (matched != null) {
            roleService.findForUser(matched.id!!, true) ?: throw UnauthorizedException()

            session.cart?.let { c -> cartService.addMultipleItems(matched.id, c) }

            call.respond(authenticationService.token(session.sessionId, null, matched.id))

        } else throw UnauthorizedException()
    }
}

@KtorExperimentalLocationsAPI
@Location("/login")
data class Login(val username: String, val password: String)