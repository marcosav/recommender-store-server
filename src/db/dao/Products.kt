package com.gmail.marcosav2010.db.dao

import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.model.PreviewProduct
import com.gmail.marcosav2010.model.Product
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant

object Products : LongIdTable() {
    val name = text("name")
    val brand = text("brand")
    val description = text("description")
    val date = timestamp("date")
    val lastUpdated = timestamp("last_updated")
    val price = double("price")
    val stock = integer("stock")
    val user = reference("user", Users)
    val visits = integer("visits").default(0)
    val rating = double("rating").default(5.0)
    val category = reference("category", ProductCategories)
    val hidden = bool("hidden").default(false)
    val deleted = bool("deleted").default(false)
}

class ProductEntity(id: EntityID<Long>) : LongEntity(id) {

    companion object : BaseEntityClass<ProductEntity>(Products) {

        fun add(product: Product) =
            new {
                name = product.name
                brand = product.brand
                description = product.description
                price = product.price
                stock = product.stock
                category = ProductCategoryEntity[product.category.id]
                hidden = product.hidden

                val d = product.date ?: Instant.now()
                date = d
                lastUpdated = d

                user = UserEntity[product.userId!!]
            }

        fun update(product: Product) =
            ProductEntity[product.id!!].apply {
                name = product.name
                brand = product.brand
                description = product.description
                price = product.price
                stock = product.stock
                category = ProductCategoryEntity[product.category.id]
                hidden = product.hidden

                lastUpdated = Instant.now()
            }

        fun delete(id: Long) {
            findById(id)?.deleted = true
        }

        fun deleteForUser(userId: Long) = Products.update({ Products.user eq userId }) {
            it[deleted] = true
        }

        fun findByIdNotDeleted(id: Long) = find { (Products.id eq id) and not(Products.deleted) }.firstOrNull()

        fun findByVendor(userId: Long, shown: Boolean) = find {
            Products.user.eq(userId)
                .and(not(Products.deleted))
                .and(
                    if (shown)
                        not(Products.hidden)
                    else
                        Products.hidden
                )
        }

        fun findByName(name: String, category: Long?) = find {
            var cond = Products.name.lowerCase().like("%${name.toLowerCase()}%")
                .and(not(Products.hidden))
                .and(not(Products.deleted))

            category?.let { cond = cond.and(Products.category eq category) }
            cond
        }
    }

    var name by Products.name
    var brand by Products.brand
    var description by Products.description
    var date by Products.date
    var lastUpdated by Products.lastUpdated
    var price by Products.price
    var stock by Products.stock
    var visits by Products.visits
    var rating by Products.rating
    var hidden by Products.hidden
    var deleted by Products.deleted

    var user by UserEntity referencedOn Products.user
    var category by ProductCategoryEntity referencedOn Products.category
    private val _images by ProductImageEntity referrersOn ProductImages.product

    private val images
        get() = _images.orderBy(Pair(ProductImages.index, SortOrder.ASC)).limit(Constants.MAX_IMAGES_PER_PRODUCT)

    fun toProduct(): Product {
        val images = images.map { it.toProductImage() }

        return Product(
            id.value,
            name,
            brand,
            price,
            stock,
            category.toCategory(),
            hidden,
            description,
            images,
            lastUpdated,
            date,
            visits,
            rating,
            user.id.value,
            user.nickname
        )
    }

    fun toPreviewProduct() =
        PreviewProduct(
            id.value,
            name,
            brand,
            price,
            stock,
            images.firstOrNull()?.uri,
            lastUpdated,
            visits,
            rating
        )
}