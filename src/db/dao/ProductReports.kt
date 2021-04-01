package com.gmail.marcosav2010.db.dao

import com.gmail.marcosav2010.model.ProductReport
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.`java-time`.timestamp
import org.jetbrains.exposed.sql.and
import java.time.Instant

object ProductReports : LongIdTable() {
    val user = reference("user", Users, onDelete = ReferenceOption.CASCADE)
    val product = reference("product", Products, onDelete = ReferenceOption.CASCADE)
    val reason = text("reason")
    val date = timestamp("date")

    init {
        uniqueIndex(user, product)
    }
}

class ProductReportEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : BaseEntityClass<ProductReportEntity>(ProductReports) {

        fun add(report: ProductReport) =
            new {
                product = ProductEntity[report.productId]
                reason = report.reason
                user = UserEntity[report.userId]
                date = Instant.now()
            }

        fun delete(id: Long) = findById(id)?.delete()

        fun findByProduct(productId: Long) = find { ProductReports.product eq productId }

        fun findByProductAndUser(productId: Long, userId: Long) =
            find { (ProductReports.product eq productId).and(ProductReports.user eq userId) }
    }

    var user by UserEntity referencedOn ProductReports.user
    var product by ProductEntity referencedOn ProductReports.product
    var reason by ProductReports.reason
    var date by ProductReports.date

    fun toReport() = ProductReport(id.value, product.id.value, reason, user.id.value, date, product.name, user.nickname)
}