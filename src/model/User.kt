package com.gmail.marcosav2010.model

import java.time.Instant

data class User(
    val id: Long?,
    val name: String,
    val surname: String,
    val email: String,
    var password: String,
    val nickname: String,
    val profileImgUri: String? = null,
    val description: String? = null,
    val registerDate: Instant? = null,
    //val deleted: Boolean? = null
)