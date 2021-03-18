package com.gmail.marcosav2010.db.dao

import com.gmail.marcosav2010.model.ProductImage
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere

object ProductImages : LongIdTable() {
    val product = reference("product", Products, onDelete = ReferenceOption.CASCADE)
    val uri = text("uri")
    val index = byte("index")

    init {
        uniqueIndex(product, index)
    }
}

class ProductImageEntity(id: EntityID<Long>) : LongEntity(id) {

    companion object : BaseEntityClass<ProductImageEntity>(ProductImages) {

        fun add(productId: Long, images: Iterable<ProductImage>) = ProductImages.batchInsert(images) {
            this[ProductImages.product] = productId
            this[ProductImages.index] = it.i
            this[ProductImages.uri] = it.u
        }

        fun remove(productId: Long) = ProductImages.deleteWhere { ProductImages.product eq productId }
    }

    var product by ProductEntity referencedOn ProductImages.product
    var uri by ProductImages.uri
    var index by ProductImages.index

    fun toProductImage() = ProductImage(index, uri)
}