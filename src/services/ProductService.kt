package com.gmail.marcosav2010.services

import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.db.dao.ProductCategoryEntity
import com.gmail.marcosav2010.db.dao.ProductEntity
import com.gmail.marcosav2010.db.dao.ProductImageEntity
import com.gmail.marcosav2010.db.dao.Products
import com.gmail.marcosav2010.model.PreviewProduct
import com.gmail.marcosav2010.model.Product
import com.gmail.marcosav2010.model.ProductCategory
import com.gmail.marcosav2010.utils.dropFrom
import db.Paged
import db.paged
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI
import org.kodein.di.instance
import kotlin.math.abs
import kotlin.math.min
import kotlin.random.Random

class ProductService(di: DI) {

    private val productStatsService by di.instance<ProductStatsService>()

    fun findById(id: Long, loadStats: Boolean = false): Product? = transaction {
        ProductEntity.findByIdNotDeleted(id)?.toProduct()?.also {
            if (loadStats) {
                val stats = productStatsService.getForProduct(id)

                it.visits = stats.visits
                it.rating = stats.rating
            }
        }
    }

    fun findByIdPreview(id: Long, hidden: Boolean = true): PreviewProduct? = transaction {
        ProductEntity.findByIdNotDeleted(id)?.takeIf { hidden or !it.hidden }?.toPreviewProduct()
    }

    fun findCategoryById(id: Long): ProductCategory? = transaction {
        ProductCategoryEntity.findById(id)?.toCategory()
    }

    fun getCategories() = transaction {
        ProductCategoryEntity.all().map { it.toCategory() }
    }

    fun findRandomByCategory(category: Long, maxAmount: Long): List<PreviewProduct> = transaction {
        val query = ProductEntity.findByCategory(category)
        val count = query.count()
        val amount = min(count, maxAmount)
        val offset = kotlin.runCatching { Random.nextLong(0, count - amount) }.getOrNull() ?: 0
        query.limit(amount.toInt(), offset).map { it.toPreviewProduct() }
    }

    private fun getProductSortingById(order: Int): Pair<Expression<*>, SortOrder> {
        return Pair(
            if (abs(order) == 2) Products.price else Products.lastUpdated,
            if (order > 0) SortOrder.ASC else SortOrder.DESC
        )
    }

    fun findByName(name: String, category: Long?, size: Int, offset: Int, order: Int): Paged<PreviewProduct> =
        transaction {
            ProductEntity.findByName(name.dropFrom(Constants.MAX_PRODUCT_NAME_LENGTH), category).paged(
                { it.toPreviewProduct() },
                size,
                offset,
                getProductSortingById(order)
            )
        }

    fun findByVendor(userId: Long, shown: Boolean, size: Int, offset: Int): Paged<PreviewProduct> = transaction {
        ProductEntity.findByVendor(userId, shown).paged(
            { it.toPreviewProduct() },
            size,
            offset,
            Pair(Products.lastUpdated, SortOrder.DESC)
        )
    }

    fun isSoldBy(productId: Long, userId: Long): Boolean = transaction {
        ProductEntity.findByIdNotDeleted(productId)?.user?.id?.value == userId
    }

    fun add(product: Product): Product = transaction {
        ProductEntity.add(product).toProduct()
            .also { ProductImageEntity.add(it.id!!, product.images) }
    }

    fun update(product: Product): Product = transaction {
        if (product.images.isNotEmpty()) {
            ProductImageEntity.remove(product.id!!, product.images.map { p -> p.i })
            ProductImageEntity.add(product.id, product.images)
        }

        ProductEntity.update(product).toProduct()
    }

    fun delete(id: Long): Unit = transaction {
        ProductEntity.delete(id)
    }

    fun deleteImages(id: Long, indices: Iterable<Byte>): Unit = transaction {
        ProductImageEntity.remove(id, indices)
        ProductEntity.deleteForUser(id)
    }

    fun deleteForUser(id: Long): Unit = transaction {
        ProductEntity.deleteForUser(id)
    }
}