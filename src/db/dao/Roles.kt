package com.gmail.marcosav2010.db.dao

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select

object UserRoles : Table() {
    val user = reference("user", Users, onDelete = ReferenceOption.CASCADE).uniqueIndex()
    val role = byte("role")

    fun findForUser(userId: Long) = select { user eq userId }.firstOrNull()?.get(role)
}