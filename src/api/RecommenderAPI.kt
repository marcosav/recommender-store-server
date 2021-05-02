package com.gmail.marcosav2010.api

import retrofit2.http.GET
import retrofit2.http.Query

interface RecommenderAPI {

    @GET("recommended")
    suspend fun getRecommendedFrom(
        @Query("user") user: Long? = null,
        @Query("item") item: Long? = null
    ): List<Long>

    @GET("popular")
    suspend fun getPopular(
        @Query("period") period: Long? = null,
        @Query("amount") amount: Long? = null
    ): List<Long>
}