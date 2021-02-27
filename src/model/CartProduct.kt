package com.gmail.marcosav2010.model

import java.time.Instant

data class CartProduct(
    val productId: Product,
    val userId: Long,
    val amount: Long,
    val lastUpdated: Instant
)