package com.gmail.marcosav2010.api

import retrofit2.http.GET
import retrofit2.http.Query

interface UserActionsAPI {

    @GET("user/rating")
    suspend fun getRatingFor(
        @Query("user") user: Long,
        @Query("item") items: Iterable<Long>
    ): Map<Long, Double>
}