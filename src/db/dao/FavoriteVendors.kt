package com.gmail.marcosav2010.db.dao

import com.gmail.marcosav2010.model.PublicUser
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

        fun findByUser(userId: Long) =
            Users.fullJoin(FavoriteVendors)
                .select {
                    not(Users.deleted).and(FavoriteVendors.user eq userId)
                }.orderBy(Pair(Users.name, SortOrder.DESC))

        inline val mapToUser
            get(): (ResultRow) -> PublicUser = {
                with(Users) {
                    PublicUser(it[id].value, it[nickname], it[description], it[profileImgUri], it[registerDate])
                }
            }

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