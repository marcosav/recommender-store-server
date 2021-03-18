package com.gmail.marcosav2010.validators

import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.routes.ProductForm
import com.gmail.marcosav2010.services.ProductService
import com.gmail.marcosav2010.utils.ImageHandler
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

        maxLength("description", it.description, Constants.MAX_PRODUCT_DESC_LENGTH)

        check("category", productService.findCategoryById(it.category) == null, "invalid")

        // TODO: improve checking check multipart

        //check("image", it.image.size > Constants.MAX_IMAGES_PER_PRODUCT, "amount.${Constants.MAX_IMAGES_PER_PRODUCT}")

        /*for (i in 0..it.image.size)
            if (allowedImageExtension("image", it.imageExt[i])) {
                val c = ImageHandler.calculateOriginalByteSize(it.image[i]) > Constants.MAX_IMAGE_BYTE_SIZE
                check("image", c, "max_size")
                if (c) break
            }*/

        it.price = "%.2f".format(it.price).toDouble()
    }
}