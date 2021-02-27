package com.gmail.marcosav2010.db.dao

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.IdTable

abstract class BaseEntityClass<out E : LongEntity>(table: IdTable<Long>) : LongEntityClass<E>(table)