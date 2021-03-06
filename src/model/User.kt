package com.gmail.marcosav2010.model

import java.time.Instant

data class User(
    val id: Long?,
    val name: String,
    val surname: String,
    val email: String,
    var password: String,
    val nickname: String,
    val description: String,
    val profileImgUri: String? = null,
    val registerDate: Instant? = null,
    val deleted: Boolean? = null
) {
    fun toPublicUser() = PublicUser(id!!, nickname, description, profileImgUri, registerDate!!)
}

data class PublicUser(
    val id: Long,
    val nickname: String,
    val description: String?,
    val profileImgUri: String?,
    val registerDate: Instant
)