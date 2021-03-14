package com.gmail.marcosav2010.services

import com.gmail.marcosav2010.db.dao.FavoriteProductEntity
import com.gmail.marcosav2010.db.dao.FavoriteVendorEntity
import com.gmail.marcosav2010.model.PreviewProduct
import com.gmail.marcosav2010.model.PublicUser
import db.Paged
import db.paged
import org.jetbrains.exposed.sql.transactions.transaction

class FavoriteService {
    fun findProductsByUser(userId: Long, size: Int, offset: Int): Paged<PreviewProduct> = transaction {
        FavoriteProductEntity.findByUser(userId).paged(
            FavoriteProductEntity.mapToPreviewProduct,
            size,
            offset
        )
    }

    fun findVendorsByUser(userId: Long, size: Int, offset: Int): Paged<PublicUser> = transaction {
        FavoriteVendorEntity.findByUser(userId).paged(
            FavoriteVendorEntity.mapToUser,
            size,
            offset
        )
    }

    fun removeProduct(productId: Long, userId: Long): Unit = transaction {
        FavoriteProductEntity.delete(userId, productId)
    }

    fun addProduct(productId: Long, userId: Long): Unit = transaction {
        if (FavoriteProductEntity.findByUserAndProduct(userId, productId).empty())
            FavoriteProductEntity.add(userId, productId)
    }

    fun removeVendor(vendorId: Long, userId: Long): Unit = transaction {
        FavoriteVendorEntity.delete(userId, vendorId)
    }

    fun addVendor(vendorId: Long, userId: Long): Unit = transaction {
        if (FavoriteVendorEntity.findByUserAndVendor(userId, vendorId).empty())
            FavoriteVendorEntity.add(userId, vendorId)
    }

    fun isFavoriteProduct(productId: Long, userId: Long): Boolean = transaction {
        !FavoriteProductEntity.findByUserAndProduct(userId, productId).empty()
    }

    fun isFavoriteVendor(vendorId: Long, userId: Long): Boolean = transaction {
        !FavoriteVendorEntity.findByUserAndVendor(userId, vendorId).empty()
    }
}