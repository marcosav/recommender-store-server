package com.gmail.marcosav2010.api

import retrofit2.http.GET
import retrofit2.http.Query

interface RecommenderAPI {

    @GET("recommended")
    suspend fun getRecommendedFor(
        @Query("user") user: Long,
        @Query("item") item: Long? = null
    ): List<Long>
}