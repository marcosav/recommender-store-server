package com.gmail.marcosav2010.services

import com.gmail.marcosav2010.db.dao.UserRoles
import com.gmail.marcosav2010.model.Role
import org.jetbrains.exposed.sql.transactions.transaction

class RoleService {

    fun findForUser(userId: Long): Role? = transaction {
        UserRoles.findForUser(userId)?.let { Role.byId(it) }
    }
}