package com.gmail.marcosav2010.services

import com.gmail.marcosav2010.db.dao.UserEntity
import com.gmail.marcosav2010.db.dao.Users
import com.gmail.marcosav2010.model.User
import org.jetbrains.exposed.sql.transactions.transaction

class UserService {

    fun findById(id: Long) = transaction {
        UserEntity[id]
    }

    fun findByNickname(nickname: String) = transaction {
        UserEntity.find { Users.nickname like "%$nickname%" }
    }

    fun add(user: User) = transaction {
        UserEntity.new {
            name = user.name
            surname = user.surname
            address = user.address
            email = user.email
            password = user.password
            profileImgUri = user.profileImgUri
            nickname = user.nickname
            description = user.description
        }
    }

    fun safeDelete(id: Long) = transaction {
        UserEntity[id].deleted = true
    }
}