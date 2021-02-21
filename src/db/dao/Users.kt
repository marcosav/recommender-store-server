package com.gmail.marcosav2010.db.dao

import com.gmail.marcosav2010.model.User
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant

object Users : LongIdTable() {
    val name = varchar("name", 63)
    val surname = varchar("surname", 63)
    val address = varchar("address", 63)
    val email = varchar("email", 63)
    val password = varchar("password", 254)
    val profileImgUri = varchar("profile_img_uri", 127).nullable()
    val nickname = varchar("nickname", 16)
    val description = varchar("description", 600)
    val registerDate = timestamp("register_date").default(Instant.now())
    val deleted = bool("deleted").default(false)
}

class UserEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserEntity>(Users)

    var name by Users.name
    var surname by Users.surname
    var address by Users.address
    var email by Users.email
    var password by Users.password
    var profileImgUri by Users.profileImgUri
    var nickname by Users.nickname
    var description by Users.description
    var registerDate by Users.registerDate
    var deleted by Users.deleted

    val products by ProductEntity referrersOn Products.user
    val favorites by FavoriteRelation referrersOn Favorites.user

    fun toUser() =
        User(
            id.value,
            name,
            surname,
            address,
            email,
            password,
            profileImgUri,
            nickname,
            description,
            registerDate,
            deleted
        )
}