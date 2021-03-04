package com.gmail.marcosav2010.db.dao

import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.model.User
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.`java-time`.timestamp
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.not
import org.jetbrains.exposed.sql.or
import java.time.Instant

object Users : LongIdTable() {
    val name = varchar("name", Constants.MAX_NAME_LENGTH)
    val surname = varchar("surname", Constants.MAX_SURNAME_LENGTH)
    val email = varchar("email", Constants.MAX_EMAIL_LENGTH)
    val password = varchar("password", 255)
    val profileImgUri = varchar("profile_img_uri", 255).nullable()
    val nickname = varchar("nickname", Constants.MAX_NICKNAME_LENGTH)
    val description = varchar("description", Constants.MAX_DESCRIPTION_LENGTH).default("")
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
            }

        fun update(user: User) =
            UserEntity[user.id!!].apply {
                name = user.name
                surname = user.surname
                email = user.email
                password = user.password
                user.profileImgUri?.let { profileImgUri = it }
                nickname = user.nickname
                user.description?.let { description = it }
            }

        fun delete(id: Long) {
            findById(id)?.deleted = true
        }

        fun findByIdNotDeleted(id: Long) =
            find { (Users.id eq id) and not(Users.deleted) }.firstOrNull()

        fun findByEmail(email: String) = find { Users.email eq email }.firstOrNull()

        fun findByExactNickname(nickname: String) = find { Users.nickname eq nickname }.firstOrNull()

        fun findByUsernameAndPassword(username: String, password: String) =
            find {
                ((Users.nickname eq username).or(Users.email eq username))
                    .and(Users.password eq password).and(not(Users.deleted))
            }.firstOrNull()

        fun findByNickname(nickname: String) = find { (Users.nickname like "%$nickname%") and not(Users.deleted) }
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
    val favoriteProducts by FavoriteProductEntity referrersOn FavoriteProducts.user
    val favoriteVendors by FavoriteVendorEntity referrersOn FavoriteVendors.user
    val cartProducts by CartProductEntity referrersOn CartProducts.user

    fun toUser() =
        User(
            id.value,
            name,
            surname,
            email,
            password,
            nickname,
            profileImgUri,
            description,
            registerDate
        )
}