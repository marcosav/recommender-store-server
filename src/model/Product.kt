package com.gmail.marcosav2010.model

import java.time.Instant

data class Product(
    val id: Long?,
    val name: String,
    val brand: String,
    val price: Double,
    var stock: Int,
    val category: ProductCategory,
    val hidden: Boolean,
    val description: String,
    var images: List<ProductImage> = emptyList(),
    val lastUpdated: Instant? = null,
    val date: Instant? = null,
    val visits: Int? = null,
    val rating: Double? = null,
    var userId: Long? = null,
    var vendorNick: String? = null,
    var fav: Boolean? = null
)

data class PreviewProduct(
    val id: Long,
    val name: String,
    val brand: String,
    val price: Double,
    val stock: Int,
    val mainImage: String?,
    val lastUpdated: Instant? = null,
    val visits: Int? = null,
    val rating: Double? = null,
    var fav: Boolean? = null
) {
    fun asDeleted() = PreviewProduct(
        id, name, brand, -1.0, -1, null
    )
}

data class ProductImage(
    val i: Byte,
    val u: String
)