package com.gmail.marcosav2010.services

import com.gmail.marcosav2010.db.dao.ProductReportEntity
import com.gmail.marcosav2010.db.dao.ProductReports
import com.gmail.marcosav2010.model.ProductReport
import db.Paged
import db.paged
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction

class ProductReportService {

    fun findByProduct(productId: Long, size: Int, offset: Int): Paged<ProductReport> = transaction {
        ProductReportEntity.findByProduct(productId).paged(
            { it.toReport() },
            size,
            offset,
            Pair(ProductReports.date, SortOrder.DESC)
        )
    }

    fun findAll(size: Int, offset: Int): Paged<ProductReport> = transaction {
        ProductReportEntity.all().paged(
            { it.toReport() },
            size,
            offset,
            Pair(ProductReports.date, SortOrder.DESC)
        )
    }

    fun delete(id: Long): Unit = transaction {
        ProductReportEntity.delete(id)
    }

    fun add(report: ProductReport): Unit = transaction {
        ProductReportEntity.add(report)
    }

    fun findById(id: Long) = transaction { ProductReportEntity.findById(id) }

    fun hasReported(productId: Long, userId: Long) =
        transaction { !ProductReportEntity.findByProductAndUser(productId, userId).empty() }
}