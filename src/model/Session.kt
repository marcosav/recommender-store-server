package com.gmail.marcosav2010.model

import com.google.gson.Gson
import io.ktor.auth.*

data class Session(
    val sessionId: String,
    var userId: Long?,
    var username: String?,
    var role: Role,
    var cart: SessionCart? = mutableListOf()
) : Principal {

    val isAdmin: Boolean get() = role == Role.ADMIN
}

typealias SessionCart = MutableList<CartProduct>

// TODO: test this
fun SessionCart.toJSON(): String = Gson().toJson(this)