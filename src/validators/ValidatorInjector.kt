package com.gmail.marcosav2010.validators

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

fun DI.MainBuilder.setupValidators() {
    bind<SignupValidator>() with singleton { SignupValidator(di) }
}