package com.gmail.marcosav2010.validators

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

fun DI.MainBuilder.setupValidators() {
    bind<UserFormValidator>() with singleton { UserFormValidator(di) }
    bind<ProductValidator>() with singleton { ProductValidator(di) }
    bind<CartUpdateValidator>() with singleton { CartUpdateValidator() }
}