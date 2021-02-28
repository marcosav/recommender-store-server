package com.gmail.marcosav2010.model

import java.time.Instant

data class Product(
    val id: Long?,
    val name: String,
    val price: Double,
    val stock: Int,
    val category: ProductCategory,
    val imgUris: String,
    val hidden: Boolean,
    val description: String? = null,
    val lastUpdated: Instant? = null,
    val date: Instant? = null,
    val visits: Int? = null,
    val deleted: Boolean? = null,
    var userId: Long? = null
)