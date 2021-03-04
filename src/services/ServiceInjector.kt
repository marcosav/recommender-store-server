package com.gmail.marcosav2010.services

import com.gmail.marcosav2010.services.cart.CartService
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

fun DI.MainBuilder.setupServices() {
    bind<AuthenticationService>() with singleton { AuthenticationService() }
    bind<UserService>() with singleton { UserService() }
    bind<RoleService>() with singleton { RoleService() }
    bind<ProductService>() with singleton { ProductService() }
    bind<CartService>() with singleton { CartService() }
    bind<FavoriteService>() with singleton { FavoriteService() }
}