package com.gmail.marcosav2010.db.dao

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant

object ProductReports : LongIdTable() {
    val user = reference("user", Users, onDelete = ReferenceOption.CASCADE)
    val product = reference("product", Products, onDelete = ReferenceOption.CASCADE)
    val reason = text("reason")
    val date = timestamp("date").default(Instant.now())

    init {
        uniqueIndex(user, product)
    }
}

class ProductReportEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : BaseEntityClass<ProductReportEntity>(ProductReports)

    var user by UserEntity referencedOn ProductReports.user
    var product by ProductEntity referencedOn ProductReports.product
    var reason by ProductReports.reason
    var date by ProductReports.date
}