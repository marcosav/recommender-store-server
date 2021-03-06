package com.gmail.marcosav2010.db.dao

import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.model.Product
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.`java-time`.timestamp
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.not
import org.jetbrains.exposed.sql.update
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
    val rating = double("rating").default(5.0)
    val category = reference("category", ProductCategories)
    val imgUris = varchar("img_uris", 3000)
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
                category = ProductCategoryEntity[product.category.id]
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
                category = ProductCategoryEntity[product.category.id]
                imgUris = product.imgUris
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
    var rating by Products.rating
    var imgUris by Products.imgUris
    var hidden by Products.hidden
    var deleted by Products.deleted

    var user by UserEntity referencedOn Products.user
    var category by ProductCategoryEntity referencedOn Products.category

    fun toProduct() =
        Product(
            id.value,
            name,
            price,
            stock,
            category.toCategory(),
            imgUris,
            hidden,
            description,
            lastUpdated,
            date,
            visits,
            rating,
            //deleted,
            user.id.value
        )
}