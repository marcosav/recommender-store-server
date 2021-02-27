package com.gmail.marcosav2010.model

import java.time.Instant

data class User(
    val id: Long? = null,
    val name: String,
    val surname: String,
    val email: String,
    val password: String,
    val profileImgUri: String? = null,
    val nickname: String,
    val description: String,
    val registerDate: Instant? = null,
    val deleted: Boolean? = null
)