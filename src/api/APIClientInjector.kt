package com.gmail.marcosav2010.api

import com.gmail.marcosav2010.Constants
import com.google.gson.GsonBuilder
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

fun DI.MainBuilder.setupAPIs() {
    bind<ProductStatsAPI>() with singleton { recommenderClient.create() }
    bind<RecommenderAPI>() with singleton { recommenderClient.create() }
    bind<UserActionsAPI>() with singleton { recommenderClient.create() }
    bind<CollectorAPI>() with singleton { recommenderClient.create() }
}

private val recommenderUrl = "${System.getenv(Constants.RECOMMENDER_SERVER)}/v${Constants.RECOMMENDER_API_VERSION}/"

private val recommenderClient = createClient()

private fun createClient(url: String = recommenderUrl) = Retrofit.Builder()
    .baseUrl(url)
    .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
    .build()