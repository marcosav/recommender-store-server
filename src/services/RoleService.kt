package com.gmail.marcosav2010.services

import com.gmail.marcosav2010.db.dao.UserRoles
import com.gmail.marcosav2010.model.Role
import org.jetbrains.exposed.sql.transactions.transaction

class RoleService {

    private val cache = mutableMapOf<Long, Role>()

    fun findForUser(userId: Long, store: Boolean = false): Role? =
        cache[userId] ?: if (store)
            findForUser0(userId)?.also { if (Role.ADMIN == it) cache[userId] = it }
        else
            Role.MEMBER

    private fun findForUser0(userId: Long): Role? = transaction {
        UserRoles.findForUser(userId)?.let { Role.byId(it) }
    }
}