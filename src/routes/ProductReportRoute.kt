package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.model.ProductReport
import com.gmail.marcosav2010.services.*
import com.gmail.marcosav2010.validators.ProductReportValidator
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.locations.post
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import kotlin.properties.Delegates

@KtorExperimentalLocationsAPI
fun Route.productReports() {

    val productService by closestDI().instance<ProductService>()
    val productReportService by closestDI().instance<ProductReportService>()
    val productReportValidator by closestDI().instance<ProductReportValidator>()

    route("/product/report") {

        post<ProductReportForm> {
            assertIdentified()

            val product = productService.findById(it.productId) ?: throw NotFoundException()

            val userId = session.userId!!
            if (userId == product.userId)
                return@post call.respond(HttpStatusCode.NoContent)

            if (product.hidden && !session.isAdmin)
                throw NotFoundException()

            it.userId = userId

            productReportValidator.validate(it)
            productReportService.add(it.toReport(userId))

            call.respond(HttpStatusCode.OK)
        }

        get<ProductReports> {
            assertAdmin()

            val offset = it.page * Constants.REPORTS_PER_PAGE
            val reports = if (it.productId == null)
                productReportService.findAll(Constants.REPORTS_PER_PAGE, offset)
            else
                productReportService.findByProduct(it.productId, Constants.REPORTS_PER_PAGE, offset)

            call.respond(reports)
        }

        delete<DeleteReport> {
            assertAdmin()

            productReportService.delete(it.id)

            call.respond(HttpStatusCode.OK)
        }
    }
}

@KtorExperimentalLocationsAPI
@Location("/{id}")
data class DeleteReport(val id: Long)

data class ProductReports(val productId: Long? = null, val page: Int = 0)

data class ProductReportForm(val productId: Long, val reason: String) {
    var userId by Delegates.notNull<Long>()

    fun toReport(userId: Long) = ProductReport(null, productId, reason, userId)
}