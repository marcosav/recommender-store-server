package com.gmail.marcosav2010.db.dao

import com.gmail.marcosav2010.model.Product
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant

object Products : LongIdTable() {
    val name = varchar("name", 63)
    val description = varchar("description", 1000)
    val date = timestamp("date").default(Instant.now())
    val lastUpdated = timestamp("last_updated").default(Instant.now())
    val price = double("price")
    val stock = integer("stock")
    val user = reference("user", Users)
    val visits = integer("visits").default(0)
    val category = integer("category")
    val imgUris = varchar("img_uris", 3000)
    val hidden = bool("hidden").default(false)
    val deleted = bool("deleted").default(false)
}

class ProductEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ProductEntity>(Products)

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
            deleted
        )
}