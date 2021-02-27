package com.gmail.marcosav2010.services

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

fun DI.MainBuilder.setupServices() {
    bind<UserService>() with singleton { UserService() }
    bind<ProductService>() with singleton { ProductService() }
    bind<CartService>() with singleton { CartService() }
}