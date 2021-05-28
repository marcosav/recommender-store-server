package com.gmail.marcosav2010

object Constants {

    /* Environment vars */
    const val PUBLIC_PATH_ENV = "PUBLIC_PATH"
    const val IMG_PATH_ENV = "IMG_PATH"
    const val JWT_SECRET_ENV = "JWT_SECRET"
    const val ALLOWED_HOSTS_ENV = "ALLOWED_HOSTS"
    const val DATABASE_SOURCE = "DATABASE_SOURCE"
    const val DATABASE_HOST = "DATABASE_HOST"
    const val DATABASE_NAME = "DATABASE_NAME"
    const val DATABASE_USER = "DATABASE_USER"
    const val DATABASE_PASSWORD = "DATABASE_PASSWORD"
    const val POPULATE = "POPULATE"

    const val RECOMMENDER_SERVER = "RECOMMENDER_SERVER"

    const val ENGINE_API_AUTH_PASSWORD = "ENGINE_API_AUTH_PASSWORD"
    const val ENGINE_API_AUTH_USER = "ENGINE_API_AUTH_USER"

    /* Static routes */
    const val DEFAULT_PUBLIC_PATH = "public"
    const val DEFAULT_IMG_PATH = "img"

    /* API Versions */
    const val API_VERSION = 1

    const val RECOMMENDER_API_VERSION = 1

    /* Input validation restrictions */
    const val MAX_EMAIL_LENGTH = 256
    const val MAX_NICKNAME_LENGTH = 20
    const val MIN_NICKNAME_LENGTH = 3
    const val MIN_PASSWORD_LENGTH = 4
    const val MAX_NAME_LENGTH = 50
    const val MAX_SURNAME_LENGTH = 63
    const val MAX_DESCRIPTION_LENGTH = 1000

    const val MAX_IMAGE_MB_SIZE = 14
    const val MAX_IMAGE_BYTE_SIZE = MAX_IMAGE_MB_SIZE * 1024 * 1024
    val ALLOWED_IMAGE_EXT = arrayOf("jpeg", "jpg", "png")

    const val MAX_ADDRESS_RECIPIENT_LENGTH = 100
    const val MAX_ADDRESS_CODE_LENGTH = 10
    const val MAX_ADDRESS_CITY_LENGTH = 63
    const val MAX_ADDRESS_REGION_LENGTH = 63
    const val MAX_ADDRESS_COUNTRY_LENGTH = 24
    const val MAX_ADDRESS_LENGTH = 100
    const val MAX_ADDRESS_PHONE_LENGTH = 32

    const val MIN_PRODUCT_NAME_LENGTH = 5
    const val MAX_PRODUCT_NAME_LENGTH = 70
    const val MIN_PRODUCT_BRAND_LENGTH = 1
    const val MAX_PRODUCT_BRAND_LENGTH = 20
    const val MAX_PRODUCT_DESC_LENGTH = 3000
    const val MIN_PRODUCT_PRICE = 0.01
    const val MAX_PRODUCT_PRICE = 999999.0
    const val MAX_PRODUCT_STOCK = 99999
    const val MAX_IMAGES_PER_PRODUCT = 8

    const val MIN_PRODUCT_REPORT_DESC_LENGTH = 20
    const val MAX_PRODUCT_REPORT_DESC_LENGTH = 300

    /* Paged items amount */
    const val PRODUCTS_PER_PAGE = 25
    const val REPORTS_PER_PAGE = 25
    const val ORDERS_PER_PAGE = 10

    /* Session claims & related */
    const val SESSION_DURATION = 7 * 24 * 3600L
    const val USER_ID_CLAIM = "uid"
    const val USERNAME_CLAIM = "username"
    const val ROLE_CLAIM = "r"
    const val CART_CLAIM = "cart"
}