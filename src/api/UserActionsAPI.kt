package com.gmail.marcosav2010.api

import retrofit2.http.GET
import retrofit2.http.Query

interface UserActionsAPI {

    @GET("user/rating")
    suspend fun getRatingFor(@Query("user") user: Long, @Query("product") products: Iterable<Long>): Map<Long, Double>
}