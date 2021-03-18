package com.gmail.marcosav2010.validators

import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.routes.UserForm

class UserEditFormValidator : Validator<UserForm>() {

    override fun validation(): ValidationContext.(UserForm) -> Unit = {
        mandatory("name", it.name)
        mandatory("surname", it.surname)
        mandatory("nickname", it.nickname)

        if (it.password.isNotBlank()) {
            mandatory("repeatedPassword", it.repeatedPassword)
            notMatching("repeatedPassword", it.password, it.repeatedPassword)
        }

        minLength("nickname", it.nickname, Constants.MIN_NICKNAME_LENGTH)
        maxLength("nickname", it.nickname, Constants.MAX_NICKNAME_LENGTH)

        maxLength("description", it.description, Constants.MAX_DESCRIPTION_LENGTH)

        minLength("password", it.password, Constants.MIN_PASSWORD_LENGTH)

        maxLength("name", it.name, Constants.MAX_NAME_LENGTH)
        maxLength("surname", it.surname, Constants.MAX_SURNAME_LENGTH)
    }
}