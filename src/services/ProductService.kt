package com.gmail.marcosav2010.services

import com.gmail.marcosav2010.db.dao.ProductEntity
import com.gmail.marcosav2010.db.dao.Products
import com.gmail.marcosav2010.model.Product
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction

class ProductService : ServiceBase() {

    fun findById(id: Long): Product? = transaction {
        ProductEntity.findById(id)?.toProduct()
    }

    fun findByName(name: String, category: Int?, size: Int, offset: Int): Pair<List<Product>, Long> =
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
        ProductEntity.findById(productId)?.user?.id?.value == userId
    }

    fun add(product: Product): Product = transaction {
        ProductEntity.add(product).toProduct()
    }

    fun update(product: Product): Unit = transaction {
        ProductEntity.update(product)
    }

    fun safeDelete(id: Long): Unit = transaction {
        ProductEntity[id].deleted = true
    }
}