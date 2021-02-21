package com.gmail.marcosav2010.db.dao

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant

object Favorites : LongIdTable() {
    val user = reference("user", Users, onDelete = ReferenceOption.CASCADE)
    val product = reference("product", Products, onDelete = ReferenceOption.CASCADE)
    val date = timestamp("date").default(Instant.now())

    init {
        uniqueIndex(user, product)
    }
}

class FavoriteRelation(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<FavoriteRelation>(Favorites)

    var user by UserEntity referencedOn Favorites.user
    var surname by ProductEntity referencedOn Favorites.product
    var registerDate by Favorites.date
}