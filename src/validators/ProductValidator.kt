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

        max("stock", it.stock, Constants.MAX_PRODUCT_STOCK)
        max("price", it.price, Constants.MAX_PRODUCT_PRICE)

        it.description?.let { e -> maxLength("description", e, Constants.MAX_PRODUCT_DESC_LENGTH) }

        check("category", productService.findCategoryById(it.category) == null, "invalid")

        // img amount
        /*val userListType: Type = object : TypeToken<List<String>>() {}.type
        val uris = Gson().fromJson<List<String>>(it.imgUris, userListType)

        check("images", uris.size > Constants.MAX_IMAGES_PER_PRODUCT, "a")*/
        // img size
    }
}