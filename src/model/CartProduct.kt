package com.gmail.marcosav2010.model

data class CartProduct(
    val id: Long,
    var amount: Long,
)

data class CartProductPreview(
    val product: PreviewProduct,
    var amount: Long,
)