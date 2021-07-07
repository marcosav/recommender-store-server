package com.gmail.marcosav2010.validators

import com.gmail.marcosav2010.routes.UpdateCartProduct

class CartUpdateValidator : Validator<UpdateCartProduct>() {

    override fun validation(): ValidationContext.(UpdateCartProduct) -> Unit = {
        max("amount", it.amount, it.product.stock)
        min("amount", it.amount, 0)
    }
}