package com.gmail.marcosav2010.model

import java.time.Instant

data class ProductStats(val visits: Long, val rating: Double, val productId: Long, val date: Instant? = null)