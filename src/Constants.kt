package com.gmail.marcosav2010

object Constants {

    /*  */
    const val MAX_EMAIL_LENGTH = 256
    const val MAX_NICKNAME_LENGTH = 20
    const val MIN_NICKNAME_LENGTH = 3
    const val MIN_PASSWORD_LENGTH = 4
    const val MAX_NAME_LENGTH = 50
    const val MAX_SURNAME_LENGTH = 63
    const val MAX_DESCRIPTION_LENGTH = 800
    const val MAX_USER_IMAGE_BYTE_SIZE = 16 * 1024 * 1024

    const val MAX_ADDRESS_RECIPIENT_LENGTH = 100
    const val MAX_ADDRESS_CODE_LENGTH = 10
    const val MAX_ADDRESS_CITY_LENGTH = 63
    const val MAX_ADDRESS_REGION_LENGTH = 63
    const val MAX_ADDRESS_COUNTRY_LENGTH = 24
    const val MAX_ADDRESS_LENGTH = 100
    const val MAX_ADDRESS_PHONE_LENGTH = 32

    const val MIN_PRODUCT_NAME_LENGTH = 6
    const val MAX_PRODUCT_NAME_LENGTH = 60
    const val MAX_PRODUCT_DESC_LENGTH = 1000
    const val MAX_PRODUCT_PRICE = 999999.0
    const val MAX_PRODUCT_STOCK = 99999
    const val MAX_IMAGES_PER_PRODUCT = 8
    const val MAX_PRODUCT_IMAGE_BYTE_SIZE = 16 * 1024 * 1024
    val ALLOWED_IMAGE_EXT = arrayOf("jpeg", "jpg", "png")

    const val MAX_PRODUCT_REPORT_DESC_LENGTH = 300

    /*  */
    const val PRODUCTS_PER_PAGE = 20
}