package com.gmail.marcosav2010.services

import com.gmail.marcosav2010.api.ProductStatsAPI
import com.gmail.marcosav2010.db.dao.ProductStatsEntity
import com.gmail.marcosav2010.model.ProductStats
import com.gmail.marcosav2010.utils.runSilent
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI
import org.kodein.di.instance
import java.time.Instant

class ProductStatsService(di: DI) {

    private val productStats by di.instance<ProductStatsAPI>()

    fun getForProduct(productId: Long): ProductStats = runBlocking {
        runSilent(ProductStats(0, .0, productId)) {
            var stats = ProductStatsEntity.findByProduct(productId)?.toStats()
            val new = stats == null
            val updated = stats?.date?.plusSeconds(VALID_TIME)?.compareTo(Instant.now()) ?: 0

            if (new || updated < 0) {
                val last = productStats.getStats(productId)
                stats = ProductStats(last.visits, last.rating, productId)

                if (new) ProductStatsEntity.add(stats) else ProductStatsEntity.update(stats)
            }

            stats!!
        }
    }

    companion object {
        private const val VALID_TIME = 15L
    }
}