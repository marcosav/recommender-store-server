package com.gmail.marcosav2010.model

import io.ktor.auth.*
import java.util.*

data class Session(
    val sessionId: UUID,
    var userId: Long?,
    var role: Role,
    var cart: MutableList<CartProduct>? = mutableListOf()
) :
    Principal {

    val isAdmin: Boolean get() = role == Role.ADMIN
}