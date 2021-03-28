package com.gmail.marcosav2010.validators

import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.routes.ProductForm
import com.gmail.marcosav2010.services.ProductService
import org.kodein.di.DI
import org.kodein.di.instance


class ProductValidator(di: DI) : Validator<ProductForm>() {

    private val productService by di.instance<ProductService>()

    override fun validation(): ValidationContext.(ProductForm) -> Unit = {
        mandatory("name", it.name)

        maxLength("name", it.name, Constants.MAX_PRODUCT_NAME_LENGTH)
        minLength("name", it.name, Constants.MIN_PRODUCT_NAME_LENGTH)

        val parsedStock = it.stock.toIntOrNull()
        format("stock", parsedStock == null)
        val parsedPrice = it.price.toDoubleOrNull()
        format("price", parsedPrice == null)

        if (parsedStock != null) {
            min("stock", parsedStock, 0)
            max("stock", parsedStock, Constants.MAX_PRODUCT_STOCK)
        }
        if (parsedPrice != null) {
            min("price", parsedPrice, Constants.MIN_PRODUCT_PRICE)
            max("price", parsedPrice, Constants.MAX_PRODUCT_PRICE)
        }

        maxLength("description", it.description, Constants.MAX_PRODUCT_DESC_LENGTH)

        check("category", productService.findCategoryById(it.category) == null, "invalid")
    }
}