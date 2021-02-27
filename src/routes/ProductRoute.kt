package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.model.Product
import com.gmail.marcosav2010.model.Session
import com.gmail.marcosav2010.services.ProductService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import org.kodein.di.instance
import org.kodein.di.ktor.di

@KtorExperimentalLocationsAPI
fun Route.product() {

    val productService by di().instance<ProductService>()

    route("/product") {

        get<Product> {
            // if hidden product check if is owner or admin, otherwise, 404
            it.userId = 1
            productService.add(it)
            call.respond(HttpStatusCode.OK)
        }

        put<Product> {
            // check if owner or admin otherwise forbidden
            // validate
            productService.update(it)
            call.respond(HttpStatusCode.OK)
        }

        get<Search> {
            val offset = it.page * Constants.PRODUCTS_PER_PAGE
            val products = productService.findByName(it.query, it.category, Constants.PRODUCTS_PER_PAGE, offset)
            call.respond(products)
        }

        delete<Delete> {
            // check if owner or admin otherwise forbidden
            productService.safeDelete(it.id)
            call.respond(HttpStatusCode.OK)
        }

        get<VendorProducts> {
            // check requester matches requested, or is admin if hidden, and user not deleted (just admin), otherwise forbidden
            val userId = it.userId ?: call.sessions.get<Session>()?.userId!!
            val offset = it.page * Constants.PRODUCTS_PER_PAGE
            val products = productService.findByVendor(userId, it.shown, Constants.PRODUCTS_PER_PAGE, offset)
            call.respond(products)
        }

        get<View> {
            // check is owner or admin if hidden, and is not deleted, otherwise 404
            val product = productService.findById(it.id)
            if (product == null)
                call.respond(HttpStatusCode.NotFound)
            else
                call.respond(product)
        }
    }
}

data class Delete(val id: Long)

data class Search(val query: String, val page: Int = 0, val category: Int? = null, val order: Int?)

@Location("/details/{id}")
data class View(val id: Long)

@Location("/vendor/{userId}")
data class VendorProducts(val userId: Long, val shown: Boolean = true, val page: Int)