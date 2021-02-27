package com.gmail.marcosav2010.db.dao

import com.gmail.marcosav2010.model.User
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.`java-time`.timestamp
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import java.time.Instant

object Users : LongIdTable() {
    val name = varchar("name", 63)
    val surname = varchar("surname", 63)
    val email = varchar("email", 63)
    val password = varchar("password", 254)
    val profileImgUri = varchar("profile_img_uri", 127).nullable()
    val nickname = varchar("nickname", 16)
    val description = varchar("description", 600)
    val registerDate = timestamp("register_date").default(Instant.now())
    val deleted = bool("deleted").default(false)
}

class UserEntity(id: EntityID<Long>) : LongEntity(id) {

    companion object : BaseEntityClass<UserEntity>(Users) {

        fun add(user: User) =
            new {
                name = user.name
                surname = user.surname
                email = user.email
                password = user.password
                profileImgUri = user.profileImgUri
                nickname = user.nickname
                description = user.description
            }

        fun update(user: User) =
            UserEntity[user.id!!].apply {
                name = user.name
                surname = user.surname
                email = user.email
                password = user.password
                profileImgUri = user.profileImgUri
                nickname = user.nickname
                description = user.description
            }

        fun findByUsernameAndPassword(username: String, password: String) =
            find {
                ((Users.nickname eq username).or(Users.email eq username))
                    .and(Users.password eq password)
            }

        fun findByNickname(nickname: String) = find { Users.nickname like "%$nickname%" }
    }

    var name by Users.name
    var surname by Users.surname
    var email by Users.email
    var password by Users.password
    var profileImgUri by Users.profileImgUri
    var nickname by Users.nickname
    var description by Users.description
    var registerDate by Users.registerDate
    var deleted by Users.deleted

    val addresses by AddressEntity referrersOn Addresses.user
    val products by ProductEntity referrersOn Products.user
    val favorites by FavoriteEntity referrersOn Favorites.user
    val cartProducts by CartProductEntity referrersOn CartProducts.user

    fun toUser() =
        User(
            id.value,
            name,
            surname,
            email,
            password,
            profileImgUri,
            nickname,
            description,
            registerDate,
            deleted
        )
}