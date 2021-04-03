package com.gmail.marcosav2010.model

import java.time.Instant

data class Order(
    val id: Long?,
    val userId: Long,
    val address: Address,
    val items: List<OrderedProduct>,
    val date: Instant? = null,
)

data class OrderedProduct(
    val amount: Int,
    val unitPrice: Double,
    val product: PreviewProduct
)