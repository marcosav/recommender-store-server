package com.gmail.marcosav2010.api

import com.gmail.marcosav2010.Constants
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.*

fun DI.MainBuilder.setupAPIs() {
    bind<ProductStatsAPI>() with singleton { recommenderClient.create() }
    bind<RecommenderAPI>() with singleton { recommenderClient.create() }
    bind<UserActionsAPI>() with singleton { recommenderClient.create() }
    bind<FeedbackAPI>() with singleton { recommenderClient.create() }
}

private val recommenderUrl = "${System.getenv(Constants.RECOMMENDER_SERVER)}/v${Constants.RECOMMENDER_API_VERSION}/"

private val recommenderClient = createClient()

private val username = getCredential(Constants.ENGINE_API_AUTH_USER)
private val password = getCredential(Constants.ENGINE_API_AUTH_PASSWORD)

private fun getCredential(env: String) =
    runCatching { System.getenv(env) }.getOrElse { throw IllegalArgumentException("Please, set $env env variable") }

private val httpClient
    get() = OkHttpClient.Builder().addInterceptor {
        val requestBuilder = it.request().newBuilder()
        val a = "Basic ${Base64.getEncoder().encodeToString("$username:$password".toByteArray(Charsets.UTF_8))}"
        requestBuilder.header("Authorization", a)
        it.proceed(requestBuilder.build())
    }.build()

private fun createClient(url: String = recommenderUrl) = Retrofit.Builder()
    .baseUrl(url)
    .client(httpClient)
    .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
    .build()