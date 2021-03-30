package com.gmail.marcosav2010.model

import java.time.Instant

data class ProductReport(
    val id: Long?,
    val productId: Long,
    val reason: String,
    val userId: Long,
    val date: Instant? = null
)
