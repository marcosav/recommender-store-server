package com.gmail.marcosav2010.model

data class CartProduct(
    val id: Long?,
    val product: Product,
    //val userId: Long,
    var amount: Long,
    //val lastUpdated: Instant
)