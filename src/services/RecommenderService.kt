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

    suspend fun getForProduct(product: Long): List<PreviewProduct> = runSilent(emptyList()) {
        recommender.getRecommendedFrom(null, product, 12)
            .mapNotNull { productService.findByIdPreview(it) }
            .ifEmpty { randomFromCategory(product) }
    }

    suspend fun getForUser(user: Long): List<PreviewProduct> = runSilent(emptyList()) {
        recommender.getRecommendedFrom(user, null, 12)
            .mapNotNull { productService.findByIdPreview(it) }
    }

    suspend fun getPopular(weekly: Boolean = true): List<PreviewProduct> = runSilent(emptyList()) {
        recommender.getPopular(if (weekly) 0 else 1, 12).mapNotNull { productService.findByIdPreview(it) }
    }

    private fun randomFromCategory(product: Long): List<PreviewProduct> {
        val category = productService.findById(product)!!.category.id
        return productService.findRandomByCategory(category, 12)
    }
}