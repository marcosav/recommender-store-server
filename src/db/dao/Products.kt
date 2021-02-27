package com.gmail.marcosav2010.db.dao

import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.model.Product
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.`java-time`.timestamp
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.not
import java.time.Instant

object Products : LongIdTable() {
    val name = varchar("name", Constants.MAX_PRODUCT_NAME_LENGTH)
    val description = varchar("description", Constants.MAX_PRODUCT_DESC_LENGTH)
    val date = timestamp("date").default(Instant.now())
    val lastUpdated = timestamp("last_updated").default(Instant.now())
    val price = double("price")
    val stock = integer("stock")
    val user = reference("user", Users)
    val visits = integer("visits").default(0)
    val category = integer("category")
    val imgUris = varchar("img_uris", Constants.MAX_PRODUCT_IMG_URIS_LENGTH)
    val hidden = bool("hidden").default(false)
    val deleted = bool("deleted").default(false)
}

class ProductEntity(id: EntityID<Long>) : LongEntity(id) {

    companion object : BaseEntityClass<ProductEntity>(Products) {

        fun add(product: Product) =
            new {
                name = product.name
                description = product.description
                price = product.price
                stock = product.stock
                category = product.category
                imgUris = product.imgUris
                hidden = product.hidden

                user = UserEntity[product.userId!!]
            }

        fun update(product: Product) =
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

        fun findByName(name: String, category: Int?) = find {
            var cond = Products.name.like("%$name%")
                .and(not(Products.hidden))
                .and(not(Products.deleted))

            category?.let { cond = cond.and(Products.category eq category) }
            cond
        }
    }

    var name by Products.name
    var description by Products.description
    var date by Products.date
    var lastUpdated by Products.lastUpdated
    var price by Products.price
    var stock by Products.stock
    var visits by Products.visits
    var category by Products.category
    var imgUris by Products.imgUris
    var hidden by Products.hidden
    var deleted by Products.deleted

    var user by UserEntity referencedOn Products.user

    fun toProduct() =
        Product(
            id.value,
            name,
            description,
            date,
            lastUpdated,
            price,
            stock,
            visits,
            category,
            imgUris,
            hidden,
            deleted,
            user.id.value
        )
}