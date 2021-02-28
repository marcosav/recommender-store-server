package com.gmail.marcosav2010.validators

import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.routes.UpdateCartProduct


class CartUpdateValidator : Validator<UpdateCartProduct>() {

    override fun validation(): ValidationContext.(UpdateCartProduct) -> Unit = {

        max("amount", it.amount, Constants.MAX_PRODUCT_STOCK.toLong())
        min("amount", it.amount, 0)
    }
}