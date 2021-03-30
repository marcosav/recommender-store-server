package com.gmail.marcosav2010.validators

import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.routes.ProductReportForm
import com.gmail.marcosav2010.services.ProductReportService
import org.kodein.di.DI
import org.kodein.di.instance


class ProductReportValidator(di: DI) : Validator<ProductReportForm>() {

    private val productReportService by di.instance<ProductReportService>()

    override fun validation(): ValidationContext.(ProductReportForm) -> Unit = {

        maxLength("reason", it.reason, Constants.MAX_PRODUCT_REPORT_DESC_LENGTH)
        minLength("reason", it.reason, Constants.MIN_PRODUCT_REPORT_DESC_LENGTH)

        check("reason", productReportService.hasReported(it.productId, it.userId), "already_reported")
    }
}