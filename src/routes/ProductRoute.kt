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
            // check owner
            productService.add(it)
            call.respond(HttpStatusCode.OK)
        }

        put<Product> {
            // check owner
            productService.update(it)
            call.respond(HttpStatusCode.OK)
        }

        get<Search> {
            val offset = it.page * Constants.PRODUCTS_PER_PAGE
            val products = productService.findBy(it.query, it.category, Constants.PRODUCTS_PER_PAGE, offset)
            call.respond(products)
        }

        delete<Delete> {
            // check requester is owner
            productService.safeDelete(it.id)
            call.respond(HttpStatusCode.OK)
        }

        get<VendorProducts> {
            // check requester matches requested if hidden, user not deleted
            val userId = it.userId ?: call.sessions.get<Session>()?.userId!!
            val offset = it.page * Constants.PRODUCTS_PER_PAGE
            val products = productService.findByVendor(userId, it.shown, Constants.PRODUCTS_PER_PAGE, offset)
            call.respond(products)
        }

        get<View> {
            // check requester is owner if hidden and not deleted
            val product = productService.findById(it.id)
            call.respond(product)
        }
    }
}

data class Delete(val id: Long)

data class Search(val query: String, val page: Int = 0, val category: Int? = null)

@Location("/details/{id}")
data class View(val id: Long)

@Location("/vendor/{userId}")
data class VendorProducts(val userId: Long, val shown: Boolean = true, val page: Int)