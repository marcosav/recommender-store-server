package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.model.Product
import com.gmail.marcosav2010.model.ProductCategory
import com.gmail.marcosav2010.services.ProductService
import com.gmail.marcosav2010.services.session
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
        val productValidator by di().instance<ProductValidator>()

        post<ProductForm> {
            productValidator.validate(it)

            var product = it.toProduct(session.userId!!)
            // TODO: update images
            product = productService.add(product)

            call.respond(product)
        }

        get<ProductSearch> {
            val offset = it.page * Constants.PRODUCTS_PER_PAGE
            val products = productService.findByName(it.query, it.category, Constants.PRODUCTS_PER_PAGE, offset)

            call.respond(products)
        }

        get<VendorProducts> {
            val userId = session.userId!!
            val admin = session.isAdmin

            if (!it.shown && it.vendorId != userId && !admin)
                throw ForbiddenException()

            val offset = it.page * Constants.PRODUCTS_PER_PAGE
            val products = productService.findByVendor(it.vendorId, it.shown, Constants.PRODUCTS_PER_PAGE, offset)

            call.respond(products)
        }

        put<ProductForm> {
            val userId = session.userId!!
            val admin = session.isAdmin
            if (!productService.isSellingBy(it.id!!, userId) && !admin)
                throw ForbiddenException()

            productValidator.validate(it)

            var product = it.toProduct()
            // TODO: update images
            product = productService.update(product)

            call.respond(product)
        }

        delete<ProductPath.Delete> {
            val userId = session.userId!!
            val admin = session.isAdmin
            if (!productService.isSellingBy(it.base.id, userId) && !admin)
                throw ForbiddenException()

            productService.delete(it.base.id)

            call.respond(HttpStatusCode.OK)
        }

        get<ProductPath.Details> {
            val product = productService.findById(it.base.id)

            val userId = session.userId!!
            val admin = session.isAdmin
            if (product == null || (product.userId != userId && product.hidden && !admin))
                throw NotFoundException()

            call.respond(product)
        }
    }
}

data class ProductForm(
    val id: Long? = null,
    val name: String,
    val description: String?,
    val price: Double,
    val stock: Int,
    val category: Long,
    val images: String,
    val hidden: Boolean
) {

    fun toProduct(userId: Long? = null) =
        Product(id, name, price, stock, ProductCategory(category), images, hidden, userId = userId)
}

@KtorExperimentalLocationsAPI
@Location("/{id}")
data class ProductPath(val id: Long) {

    @Location("/")
    data class Details(val base: ProductPath)

    @Location("/")
    data class Delete(val base: ProductPath)
}

@KtorExperimentalLocationsAPI
@Location("/list")
data class ProductSearch(val query: String, val page: Int = 0, val category: Long? = null, val order: Int? = null)

@KtorExperimentalLocationsAPI
@Location("/vendor/{vendorId}")
data class VendorProducts(val vendorId: Long, val shown: Boolean = true, val page: Int)