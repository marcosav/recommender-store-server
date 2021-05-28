package com.gmail.marcosav2010.services

import com.gmail.marcosav2010.services.cart.CartService
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

fun DI.MainBuilder.setupServices() {
    bind<AuthenticationService>() with singleton { AuthenticationService() }
    bind<UserService>() with singleton { UserService(di) }
    bind<RoleService>() with singleton { RoleService() }
    bind<ProductService>() with singleton { ProductService(di) }
    bind<CartService>() with singleton { CartService(di) }
    bind<FavoriteService>() with singleton { FavoriteService() }
    bind<ProductReportService>() with singleton { ProductReportService() }
    bind<OrderService>() with singleton { OrderService() }
    bind<ProductStatsService>() with singleton { ProductStatsService(di) }
    bind<FeedbackService>() with singleton { FeedbackService(di) }
    bind<RecommenderService>() with singleton { RecommenderService(di) }
    bind<CheckoutService>() with singleton { CheckoutService(di) }
}