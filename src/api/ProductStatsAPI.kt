package com.gmail.marcosav2010.api

import com.gmail.marcosav2010.model.ProductStats
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductStatsAPI {

    @GET("stats")
    suspend fun getStats(@Query("item") item: Long): ProductStats
}