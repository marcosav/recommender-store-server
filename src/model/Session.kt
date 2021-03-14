package com.gmail.marcosav2010.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.auth.*
import java.util.*


data class Session(
    val sessionId: String,
    var userId: Long?,
    var username: String?,
    var role: Role,
    var cart: SessionCart = listOf()
) : Principal {

    val isAdmin: Boolean get() = role == Role.ADMIN
}

typealias SessionCart = List<CartProduct>

fun SessionCart.toJSON(): String = Gson().toJson(this)

fun fromJSON(serialized: String): SessionCart {
    val type = object : TypeToken<ArrayList<CartProduct>>() {}.type
    return Gson().fromJson(serialized, type)
}