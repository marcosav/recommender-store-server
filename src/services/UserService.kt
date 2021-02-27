package com.gmail.marcosav2010.services

import com.gmail.marcosav2010.db.dao.UserEntity
import com.gmail.marcosav2010.model.Address
import com.gmail.marcosav2010.model.User
import org.jetbrains.exposed.sql.transactions.transaction

class UserService : ServiceBase() {

    fun findById(id: Long): User? = transaction {
        UserEntity.findById(id)?.toUser()
    }

    fun findByNickname(nickname: String, size: Int, offset: Int): Pair<List<User>, Long> =
        findPaged(UserEntity.findByNickname(nickname), { it.toUser() }, size, offset)

    fun findByUsernameAndPassword(username: String, password: String): User? = transaction {
        UserEntity.findByUsernameAndPassword(username, password).firstOrNull()?.toUser()
    }

    fun findUserAddresses(id: Long): List<Address> = transaction {
        UserEntity.findById(id)?.addresses?.map { it.toAddress() } ?: emptyList()
    }

    fun add(user: User): User = transaction {
        UserEntity.add(user).toUser()
    }

    fun update(user: User): Unit = transaction {
        UserEntity.update(user)
    }

    fun safeDelete(id: Long): Unit = transaction {
        UserEntity[id].deleted = true
    }
}