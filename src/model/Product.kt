package com.gmail.marcosav2010.model

import java.time.Instant

data class Product(
    val id: Long? = null,
    val name: String,
    val description: String,
    val date: Instant? = null,
    val lastUpdated: Instant? = null,
    val price: Double,
    val stock: Int,
    val visits: Int? = null,
    val category: Int,
    val imgUris: String,
    val hidden: Boolean,
    val deleted: Boolean? = null,
    var userId: Long? = null
)