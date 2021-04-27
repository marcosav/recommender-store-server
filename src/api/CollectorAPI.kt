package com.gmail.marcosav2010.api

import retrofit2.http.POST
import retrofit2.http.Query

interface CollectorAPI {

    @POST("record")
    suspend fun collect(
        @Query("sessionId") sessionId: String,
        @Query("user") user: Long,
        @Query("item") item: Long,
        @Query("action") action: Int,
        @Query("value") value: Double? = null
    )
}