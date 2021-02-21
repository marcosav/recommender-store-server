package com.gmail.marcosav2010.services

import com.gmail.marcosav2010.db.dao.ProductEntity
import com.gmail.marcosav2010.db.dao.Products
import com.gmail.marcosav2010.model.Product
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.not
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class ProductService {

    fun findBy(name: String, category: Int?, size: Int, offset: Int) = transaction {
        val query = ProductEntity
            .find {
                var cond = Products.name.like("%$name%")
                    .and(not(Products.hidden))
                    .and(not(Products.deleted))

                category?.let { cond = cond.and(Products.category eq category) }
                cond
            }

        val total = query.count()

        val products = query.orderBy(Pair(Products.lastUpdated, SortOrder.DESC))
            .limit(size, offset.toLong())
            .map { it.toProduct() }

        mapOf("products" to products, "total" to total)
    }

    fun findById(id: Long) = transaction {
        ProductEntity[id]
    }

    fun add(product: Product) = transaction {
        ProductEntity.new {
            name = product.name
            description = product.description
            price = product.price
            stock = product.stock
            category = product.category
            imgUris = product.imgUris
            hidden = product.hidden
        }
    }

    fun update(product: Product) = transaction {
        ProductEntity[product.id!!].apply {
            name = product.name
            description = product.description
            price = product.price
            stock = product.stock
            category = product.category
            imgUris = product.imgUris
            hidden = product.hidden

            lastUpdated = Instant.now()
        }
    }

    fun safeDelete(id: Long) = transaction {
        ProductEntity[id].deleted = true
    }

    fun findByVendor(userId: Long, shown: Boolean, size: Int, offset: Int) = transaction {
        val query = ProductEntity
            .find {
                Products.user.eq(userId)
                    .and(not(Products.deleted))
                    .and(
                        if (shown)
                            not(Products.hidden)
                        else
                            Products.hidden
                    )
            }

        val total = query.count()

        val products = query.orderBy(Pair(Products.lastUpdated, SortOrder.DESC))
            .limit(size, offset.toLong())
            .map { it.toProduct() }

        mapOf("products" to products, "total" to total)
    }
}