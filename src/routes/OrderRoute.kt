package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.services.OrderService
import com.gmail.marcosav2010.services.UserService
import com.gmail.marcosav2010.services.assertIdentified
import com.gmail.marcosav2010.services.session
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

@KtorExperimentalLocationsAPI
fun Route.orders() {

    val userService by closestDI().instance<UserService>()
    val orderService by closestDI().instance<OrderService>()

    get<OrdersPath> {
        assertIdentified()

        val currentUser = session.userId!!
        val userId = it.userId ?: currentUser
        if (userId != currentUser && !session.isAdmin)
            throw NotFoundException()

        userService.findById(userId) ?: throw NotFoundException()

        val offset = it.page * Constants.ORDERS_PER_PAGE
        val orders = orderService.findByUser(userId, Constants.ORDERS_PER_PAGE, offset)

        call.respond(orders)
    }
}

@KtorExperimentalLocationsAPI
@Location("/orders")
data class OrdersPath(val userId: Long? = null, val page: Int = 0)