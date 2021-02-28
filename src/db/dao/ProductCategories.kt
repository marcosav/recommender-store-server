package com.gmail.marcosav2010.db.dao

import com.gmail.marcosav2010.model.ProductCategory
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object ProductCategories : LongIdTable() {
    val name = varchar("name", 50).uniqueIndex()
}

class ProductCategoryEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : BaseEntityClass<ProductCategoryEntity>(ProductCategories)

    var name by ProductCategories.name

    val products by ProductEntity referrersOn Products.category

    fun toCategory() = ProductCategory(id.value, name)
}