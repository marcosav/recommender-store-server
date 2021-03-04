package com.gmail.marcosav2010.db.dao

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant

object FavoriteVendors : LongIdTable() {
    val user = reference("user", Users, onDelete = ReferenceOption.CASCADE)
    val vendor = reference("vendor", Users, onDelete = ReferenceOption.CASCADE)
    val date = timestamp("date").default(Instant.now())

    init {
        uniqueIndex(user, vendor)
    }
}

class FavoriteVendorEntity(id: EntityID<Long>) : LongEntity(id) {

    companion object : BaseEntityClass<FavoriteVendorEntity>(FavoriteVendors) {

        // TODO: check this
        fun findByUser(userId: Long) =
            UserEntity.find {
                Users.id inSubQuery (FavoriteVendors.slice(FavoriteVendors.id)
                    .select { FavoriteVendors.user eq userId })
            }.orderBy(Pair(FavoriteVendors.date, SortOrder.DESC))

        fun findByUserAndVendor(userId: Long, vendorId: Long) =
            find { (FavoriteVendors.user eq userId) and (FavoriteVendors.vendor eq vendorId) }

        fun add(userId: Long, vendorId: Long) =
            new {
                vendor = UserEntity[vendorId]
                user = UserEntity[userId]
            }

        fun delete(userId: Long, vendorId: Long) =
            FavoriteVendors.deleteWhere { (FavoriteVendors.user eq userId) and (FavoriteVendors.vendor eq vendorId) }
    }

    var user by UserEntity referencedOn FavoriteVendors.user
    var vendor by UserEntity referencedOn FavoriteVendors.vendor
    var date by FavoriteProducts.date
}