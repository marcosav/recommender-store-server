package com.gmail.marcosav2010.services

import com.gmail.marcosav2010.db.dao.FavoriteProductEntity
import com.gmail.marcosav2010.db.dao.FavoriteVendorEntity
import com.gmail.marcosav2010.model.PreviewProduct
import com.gmail.marcosav2010.model.User
import org.jetbrains.exposed.sql.transactions.transaction

class FavoriteService {

    fun findProductsByUser(userId: Long): List<PreviewProduct> = transaction {
        FavoriteProductEntity.findByUser(userId).map { it.toPreviewProduct() }
    }

    fun findVendorsByUser(userId: Long): List<User> = transaction {
        FavoriteVendorEntity.findByUser(userId).map { it.toUser() }
    }

    fun removeProduct(productId: Long, userId: Long): Unit = transaction {
        FavoriteProductEntity.delete(userId, productId)
    }

    fun addProduct(productId: Long, userId: Long): Unit = transaction {
        if (FavoriteProductEntity.findByUserAndProduct(userId, productId).count() == 0L)
            FavoriteProductEntity.add(userId, productId)
    }

    fun removeVendor(vendorId: Long, userId: Long): Unit = transaction {
        FavoriteVendorEntity.delete(userId, vendorId)
    }

    fun addVendor(vendorId: Long, userId: Long): Unit = transaction {
        if (FavoriteVendorEntity.findByUserAndVendor(userId, vendorId).count() == 0L)
            FavoriteVendorEntity.add(userId, vendorId)
    }
}