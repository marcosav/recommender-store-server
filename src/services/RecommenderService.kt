package com.gmail.marcosav2010.services

import com.gmail.marcosav2010.api.RecommenderAPI
import com.gmail.marcosav2010.model.PreviewProduct
import com.gmail.marcosav2010.utils.runSilent
import kotlinx.coroutines.runBlocking
import org.kodein.di.DI
import org.kodein.di.instance

class RecommenderService(di: DI) {

    private val recommender by di.instance<RecommenderAPI>()
    private val productService by di.instance<ProductService>()

    fun getFor(user: Long? = null, product: Long? = null): List<PreviewProduct> = runBlocking {
        runSilent(emptyList()) {
            val products = recommender.getRecommendedFrom(user, product, 12)

            products.mapNotNull { productService.findByIdPreview(it) }
        }
    }

    fun getPopular(weekly: Boolean = true): List<PreviewProduct> = runBlocking {
        runSilent(emptyList()) {
            val products = recommender.getPopular(if (weekly) 0 else 1, 12)

            products.mapNotNull { productService.findByIdPreview(it) }
        }
    }
}