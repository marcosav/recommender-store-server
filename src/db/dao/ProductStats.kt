package com.gmail.marcosav2010.db.dao

import com.gmail.marcosav2010.model.ProductStats
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant

object ProductStatsTable : LongIdTable("productstats") {
    val visits = long("visits")
    val rating = double("rating")
    val product = reference("product", Products, onDelete = ReferenceOption.CASCADE).uniqueIndex()
    val date = timestamp("update_date")
}

class ProductStatsEntity(id: EntityID<Long>) : LongEntity(id) {

    companion object : BaseEntityClass<ProductStatsEntity>(ProductStatsTable) {

        fun add(stats: ProductStats) =
            new {
                visits = stats.visits
                rating = stats.rating
                product = ProductEntity[stats.productId]
                date = Instant.now()
            }

        fun update(stats: ProductStats) =
            findByProduct(stats.productId)?.apply {
                visits = stats.visits
                rating = stats.rating
                date = Instant.now()
            }

        fun findByProduct(productId: Long) = find { ProductStatsTable.product eq productId }.firstOrNull()
    }

    var visits by ProductStatsTable.visits
    var rating by ProductStatsTable.rating
    var date by ProductStatsTable.date

    var product by ProductEntity referencedOn ProductStatsTable.product

    fun toStats() = ProductStats(visits, rating, product.id.value, date)
}