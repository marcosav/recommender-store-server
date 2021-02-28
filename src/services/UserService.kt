package com.gmail.marcosav2010.services

import com.gmail.marcosav2010.db.dao.UserEntity
import com.gmail.marcosav2010.model.Address
import com.gmail.marcosav2010.model.User
import com.gmail.marcosav2010.utils.BCryptEncoder
import org.jetbrains.exposed.sql.transactions.transaction

class UserService : ServiceBase() {

    fun findById(id: Long): User? = transaction {
        UserEntity.findByIdNotDeleted(id)?.toUser()
    }

    fun findByEmail(email: String): User? = transaction {
        UserEntity.findByEmail(email)?.toUser()
    }

    fun findByExactNickname(nickname: String): User? = transaction {
        UserEntity.findByExactNickname(nickname)?.toUser()
    }

    fun findByNickname(nickname: String, size: Int, offset: Int): Pair<List<User>, Long> =
        findPaged(UserEntity.findByNickname(nickname), { it.toUser() }, size, offset)

    fun findByUsernameAndPassword(username: String, password: String): User? = transaction {
        val hashed = BCryptEncoder.encode(password)
        UserEntity.findByUsernameAndPassword(username, hashed)?.toUser()
    }

    fun findUserAddresses(id: Long): List<Address> = transaction {
        UserEntity.findByIdNotDeleted(id)?.addresses?.map { it.toAddress() } ?: emptyList()
    }

    fun add(user: User): User = transaction {
        user.password = BCryptEncoder.encode(user.password)
        UserEntity.add(user).toUser()
    }

    fun update(user: User): User = transaction {
        user.password = BCryptEncoder.encode(user.password)
        UserEntity.update(user).toUser()
    }

    fun delete(id: Long): Unit = transaction {
        UserEntity.delete(id)
    }
}