package com.gmail.marcosav2010.services

import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.db.dao.UserEntity
import com.gmail.marcosav2010.model.Address
import com.gmail.marcosav2010.model.PublicUser
import com.gmail.marcosav2010.model.User
import com.gmail.marcosav2010.utils.BCryptEncoder
import com.gmail.marcosav2010.utils.dropFrom
import db.Paged
import db.paged
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI
import org.kodein.di.instance

class UserService(di: DI) {

    private val productService by di.instance<ProductService>()

    fun findById(id: Long): User? = transaction {
        UserEntity.findByIdNotDeleted(id)?.toUser()
    }

    fun findByEmail(email: String): User? = transaction {
        UserEntity.findByEmail(email.dropFrom(Constants.MAX_EMAIL_LENGTH))?.toUser()
    }

    fun findByExactNickname(nickname: String): User? = transaction {
        UserEntity.findByExactNickname(nickname.dropFrom(Constants.MAX_NICKNAME_LENGTH))?.toUser()
    }

    fun findByNickname(nickname: String, size: Int, offset: Int): Paged<PublicUser> = transaction {
        UserEntity.findByNickname(nickname.dropFrom(Constants.MAX_NICKNAME_LENGTH))
            .paged({ it.toUser().toPublicUser() }, size, offset)
    }

    fun findByUsername(username: String): User? =
        transaction { UserEntity.findByUsername(username.dropFrom(Constants.MAX_EMAIL_LENGTH))?.toUser() }

    fun findUserAddresses(id: Long): List<Address> = transaction {
        UserEntity.findByIdNotDeleted(id)?.addresses?.map { it.toAddress() } ?: emptyList()
    }

    fun add(user: User): User {
        user.password = BCryptEncoder.encode(user.password)
        return transaction { UserEntity.add(user).toUser() }
    }

    fun update(user: User): User {
        if (user.password.isNotBlank())
            user.password = BCryptEncoder.encode(user.password)
        return transaction { UserEntity.update(user).toUser() }
    }

    fun delete(id: Long): Unit = transaction {
        UserEntity.delete(id)
        productService.deleteForUser(id)
    }
}