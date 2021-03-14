package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.model.Product
import com.gmail.marcosav2010.model.ProductCategory
import com.gmail.marcosav2010.model.ProductImage
import com.gmail.marcosav2010.services.FavoriteService
import com.gmail.marcosav2010.services.ProductService
import com.gmail.marcosav2010.services.assertIdentified
import com.gmail.marcosav2010.services.session
import com.gmail.marcosav2010.utils.ImageHandler
import com.gmail.marcosav2010.validators.ProductValidator
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.di

@KtorExperimentalLocationsAPI
fun Route.product() {

    route("/product") {

        val productService by di().instance<ProductService>()
        val favoriteService by di().instance<FavoriteService>()
        val productValidator by di().instance<ProductValidator>()

        post<ProductForm> {
            assertIdentified()
            productValidator.validate(it)

            var product = it.toProduct(session.userId!!)
            it.image.forEachIndexed { i, im -> it.images[i] = ImageHandler.process(im, it.imageExt[i]) }
            product = productService.add(product)

            call.respond(product)
        }

        get<ProductSearch> {
            val offset = it.page * Constants.PRODUCTS_PER_PAGE
            val products = productService.findByName(it.query, it.category, Constants.PRODUCTS_PER_PAGE, offset)
            session.userId?.let {
                products.items.onEach { p -> p.fav = favoriteService.isFavoriteProduct(p.id, it) }
            }

            call.respond(products)
        }

        get<VendorProducts> {
            if (!it.shown)
                assertIdentified()

            if (!it.shown && it.vendorId != session.userId!! && !session.isAdmin)
                throw ForbiddenException()

            val offset = it.page * Constants.PRODUCTS_PER_PAGE
            val products = productService.findByVendor(it.vendorId, it.shown, Constants.PRODUCTS_PER_PAGE, offset)

            call.respond(products)
        }

        put<ProductForm> {
            assertIdentified()

            val userId = session.userId!!
            val admin = session.isAdmin
            if (!productService.isSoldBy(it.id!!, userId) && !admin)
                throw ForbiddenException()

            productValidator.validate(it)

            var product = it.toProduct()
            it.image.forEachIndexed { i, im -> it.images[i] = ImageHandler.process(im, it.imageExt[i]) }
            product = productService.update(product)

            call.respond(product)
        }

        delete<ProductPath.Delete> {
            assertIdentified()

            val userId = session.userId!!
            val admin = session.isAdmin
            if (!productService.isSoldBy(it.base.id, userId) && !admin)
                throw ForbiddenException()

            productService.delete(it.base.id)

            call.respond(HttpStatusCode.OK)
        }

        get<ProductPath.Details> {
            val product = productService.findById(it.base.id)

            val admin = session.isAdmin
            if (product == null || (product.hidden && product.userId != session.userId && !admin))
                throw NotFoundException()

            session.userId?.let { uid ->
                product.fav = favoriteService.isFavoriteProduct(product.id!!, uid)
            }

            call.respond(product)
        }
    }
}

data class ProductForm(
    val id: Long? = null,
    val name: String,
    val description: String,
    var price: Double,
    val stock: Int,
    val category: Long,
    val hidden: Boolean,
    val image: List<String>,
    val imageExt: List<String>
) {
    val images = mutableListOf<String>()

    fun toProduct(userId: Long? = null) =
        Product(
            id,
            name,
            price,
            stock,
            ProductCategory(category),
            hidden,
            description,
            images.mapIndexed { i, s -> ProductImage(i.toByte(), s) },
            userId = userId
        )
}

@KtorExperimentalLocationsAPI
@Location("/{id}")
data class ProductPath(val id: Long) {

    data class Details(val base: ProductPath)

    data class Delete(val base: ProductPath)
}

@KtorExperimentalLocationsAPI
@Location("/list")
data class ProductSearch(val query: String, val page: Int = 0, val category: Long? = null, val order: Int? = null)

@KtorExperimentalLocationsAPI
@Location("/vendor/{vendorId}")
data class VendorProducts(val vendorId: Long, val shown: Boolean = true, val page: Int)