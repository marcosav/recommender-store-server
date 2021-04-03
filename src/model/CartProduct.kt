package com.gmail.marcosav2010.model

data class CartProduct(
    val id: Long,
    var amount: Int,
)

data class CartProductPreview(
    var product: PreviewProduct,
    var amount: Int,
    var unavailable: Boolean? = null
)