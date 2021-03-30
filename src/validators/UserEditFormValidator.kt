package com.gmail.marcosav2010.validators

import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.routes.BadRequestException
import com.gmail.marcosav2010.routes.UserForm
import com.gmail.marcosav2010.services.UserService
import org.kodein.di.DI
import org.kodein.di.instance

class UserEditFormValidator(di: DI) : Validator<UserForm>() {

    private val userService by di.instance<UserService>()

    override fun validation(): ValidationContext.(UserForm) -> Unit = {
        mandatory("name", it.name)
        mandatory("surname", it.surname)
        mandatory("nickname", it.nickname)

        if (it.password.isNotBlank()) {
            mandatory("repeatedPassword", it.repeatedPassword)
            notMatching("repeatedPassword", it.password, it.repeatedPassword)

            minLength("password", it.password, Constants.MIN_PASSWORD_LENGTH)
        }

        minLength("nickname", it.nickname, Constants.MIN_NICKNAME_LENGTH)
        maxLength("nickname", it.nickname, Constants.MAX_NICKNAME_LENGTH)

        maxLength("description", it.description, Constants.MAX_DESCRIPTION_LENGTH)

        maxLength("name", it.name, Constants.MAX_NAME_LENGTH)
        maxLength("surname", it.surname, Constants.MAX_SURNAME_LENGTH)

        val user = userService.findById(it.id!!) ?: throw BadRequestException()
        if (user.nickname != it.nickname)
            existing("nickname", userService.findByExactNickname(it.nickname) != null)
    }
}