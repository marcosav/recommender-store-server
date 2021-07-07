package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.model.Product
import com.gmail.marcosav2010.model.ProductCategory
import com.gmail.marcosav2010.model.ProductImage
import com.gmail.marcosav2010.services.*
import com.gmail.marcosav2010.utils.ImageHandler
import com.gmail.marcosav2010.utils.OversizeImageException
import com.gmail.marcosav2010.utils.step
import com.gmail.marcosav2010.validators.ImageValidator
import com.gmail.marcosav2010.validators.ProductValidator
import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.routing.put
import io.ktor.routing.post
import io.ktor.util.pipeline.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

@KtorExperimentalLocationsAPI
fun Route.product() {

    val productService by closestDI().instance<ProductService>()
    val orderService by closestDI().instance<OrderService>()
    val favoriteService by closestDI().instance<FavoriteService>()
    val collectorService by closestDI().instance<FeedbackService>()

    val productValidator by closestDI().instance<ProductValidator>()
    val imageValidator by closestDI().instance<ImageValidator>()

    route("/product") {

        suspend fun PipelineContext<Unit, ApplicationCall>.handleMultipart(
            success: (Pair<ProductForm, Array<String?>>) -> Product,
            formCheck: (ProductForm) -> Unit = {}
        ) {
            val multipart = call.receiveMultipart()

            var form: ProductForm? = null
            val images = arrayOfNulls<String>(Constants.MAX_IMAGES_PER_PRODUCT)
            val deleted = mutableSetOf<Byte>()

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        if (part.name == "form") {
                            form = parseForm(part.value)

                            formCheck(form!!)

                            productValidator.validate(form!!)

                        } else if (part.name?.startsWith("delete") == true) {
                            val index = part.name?.replace("delete", "")?.toByteOrNull()
                                ?: throw BadRequestException()

                            if (index < 0 || index >= Constants.MAX_IMAGES_PER_PRODUCT)
                                throw BadRequestException()

                            deleted.add(index)
                        }
                    }
                    is PartData.FileItem -> {
                        if (part.name?.startsWith("file") != true) return@forEachPart

                        val index = part.name?.replace("file", "")?.toIntOrNull()
                            ?: throw BadRequestException()

                        if (index < 0 || index >= Constants.MAX_IMAGES_PER_PRODUCT)
                            throw BadRequestException()

                        part.originalFileName?.let { filename ->
                            imageValidator.validate(filename)
                            images[index] = try {
                                ImageHandler.process(part)
                            } catch (ex: OversizeImageException) {
                                imageValidator.onOversize()
                            }
                        }
                    }
                    else -> {
                    }
                }

                part.dispose()
            }

            form!!.deletedImages = deleted
            val id = success(Pair(form!!, images)).id

            requireNotNull(id)
            call.respond(id)
        }

        get("/categories") {
            call.respond(productService.getCategories())
        }

        post {
            assertIdentified()

            handleMultipart({ e ->
                productService.add(e.first.toProduct(e.second, session.userId!!))
            }) {}
        }

        get<ProductSearch> {
            val offset = it.page * Constants.PRODUCTS_PER_PAGE
            val products =
                productService.findByName(it.query, it.category, Constants.PRODUCTS_PER_PAGE, offset, it.order)
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

        put {
            assertIdentified()

            handleMultipart({
                productService.deleteImages(it.first.id!!, it.first.deletedImages)
                productService.update(it.first.toProduct(it.second))
            }) {
                val userId = session.userId!!
                val admin = session.isAdmin
                if (!productService.isSoldBy(it.id!!, userId) && !admin)
                    throw ForbiddenException()
            }
        }

        put<ProductPath.Rate> {
            assertIdentified()

            if (!orderService.hasBought(session.userId!!, it.base.id)) throw BadRequestException()
            if (!(0.5..5.0).step(0.5).contains(it.rating)) throw BadRequestException()

            collectorService.collectRating(session, it.base.id, it.rating)

            call.respond(HttpStatusCode.OK)
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
            val product = productService.findById(it.base.id, true)

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

private fun parseForm(str: String): ProductForm = Gson().fromJson(str, ProductForm::class.java)

data class ProductForm(
    val id: Long? = null,
    val name: String,
    val brand: String,
    val description: String,
    val price: String,
    val stock: String,
    val category: Long,
    val hidden: Boolean,
) {
    lateinit var deletedImages: Set<Byte>

    fun toProduct(images: Array<String?>, userId: Long? = null) =
        Product(
            id,
            name,
            brand,
            "%.2f".format(price.toDouble()).toDouble(),
            stock.toInt(),
            ProductCategory(category),
            hidden,
            description,
            images.mapIndexed { i, s -> s?.let { ProductImage(i.toByte(), s) } }.filterNotNull(),
            userId = userId
        )
}

@KtorExperimentalLocationsAPI
@Location("/{id}")
data class ProductPath(val id: Long) {

    data class Details(val base: ProductPath)

    data class Delete(val base: ProductPath)

    @Location("/rate")
    data class Rate(val base: ProductPath, val rating: Double)
}

@KtorExperimentalLocationsAPI
@Location("/list")
data class ProductSearch(val query: String, val page: Int = 0, val category: Long? = null, val order: Int = 0)

@KtorExperimentalLocationsAPI
@Location("/vendor/{vendorId}")
data class VendorProducts(val vendorId: Long, val shown: Boolean = true, val page: Int = 0)