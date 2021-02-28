package com.gmail.marcosav2010.services

import com.gmail.marcosav2010.db.dao.ProductCategoryEntity
import com.gmail.marcosav2010.db.dao.ProductEntity
import com.gmail.marcosav2010.db.dao.Products
import com.gmail.marcosav2010.model.Product
import com.gmail.marcosav2010.model.ProductCategory
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction

class ProductService : ServiceBase() {

    fun findById(id: Long): Product? = transaction {
        ProductEntity.findByIdNotDeleted(id)?.toProduct()
    }

    fun findCategoryById(id: Long): ProductCategory? = transaction {
        ProductCategoryEntity.findById(id)?.toCategory()
    }

    fun getCategories() = transaction {
        ProductCategoryEntity.all().map { it.toCategory() }
    }

    fun findByName(name: String, category: Long?, size: Int, offset: Int): Pair<List<Product>, Long> =
        findPaged(
            ProductEntity.findByName(name, category),
            { it.toProduct() },
            size,
            offset,
            Pair(Products.lastUpdated, SortOrder.DESC)
        )

    fun findByVendor(userId: Long, shown: Boolean, size: Int, offset: Int): Pair<List<Product>, Long> =
        findPaged(
            ProductEntity.findByVendor(userId, shown),
            { it.toProduct() },
            size,
            offset,
            Pair(Products.lastUpdated, SortOrder.DESC)
        )

    fun isSelling(productId: Long, userId: Long): Boolean = transaction {
        ProductEntity.findByIdNotDeleted(productId)?.user?.id?.value == userId
    }

    fun add(product: Product): Product = transaction {
        ProductEntity.add(product).toProduct()
    }

    fun update(product: Product): Product = transaction {
        ProductEntity.update(product).toProduct()
    }

    fun delete(id: Long): Unit = transaction {
        ProductEntity.delete(id)
    }

    fun deleteForUser(id: Long): Unit = transaction {
        ProductEntity.deleteForUser(id)
    }
}