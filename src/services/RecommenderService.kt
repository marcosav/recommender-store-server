package com.gmail.marcosav2010.services

import com.gmail.marcosav2010.api.RecommenderAPI
import com.gmail.marcosav2010.model.PreviewProduct
import kotlinx.coroutines.runBlocking
import org.kodein.di.DI
import org.kodein.di.instance

class RecommenderService(di: DI) {

    private val recommender by di.instance<RecommenderAPI>()
    private val productService by di.instance<ProductService>()

    fun getFor(user: Long, product: Long? = null): List<PreviewProduct> = runBlocking {
        val products = recommender.getRecommendedFor(user, product)

        products.mapNotNull { productService.findByIdPreview(it) }
    }
}