package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.model.PreviewProduct
import com.gmail.marcosav2010.services.FavoriteService
import com.gmail.marcosav2010.services.RecommenderService
import com.gmail.marcosav2010.services.session
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

@KtorExperimentalLocationsAPI
fun Route.recommended() {

    val recommenderService by closestDI().instance<RecommenderService>()
    val favoriteService by closestDI().instance<FavoriteService>()

    fun PipelineContext<*, ApplicationCall>.applyFavorites(products: List<PreviewProduct>) =
        session.userId?.let { uid ->
            products.onEach { p -> p.fav = favoriteService.isFavoriteProduct(p.id, uid) }
        }

    get<RecommendedRoute> {
        val user = session.userId

        val products = when {
            it.product != null -> recommenderService.getForProduct(it.product)
            user != null -> recommenderService.getForUser(user)
            else -> throw BadRequestException()
        }

        applyFavorites(products)

        call.respond(products)
    }

    get("/popular") {
        val products = recommenderService.getPopular()
        applyFavorites(products)
        call.respond(products)
    }
}

@KtorExperimentalLocationsAPI
@Location("/recommended")
data class RecommendedRoute(val product: Long? = null)