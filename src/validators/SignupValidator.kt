package com.gmail.marcosav2010.validators

import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.routes.Signup
import com.gmail.marcosav2010.services.UserService
import org.kodein.di.DI
import org.kodein.di.instance
import java.util.regex.Pattern

class SignupValidator(di: DI) : Validator<Signup>() {

    companion object {
        private val EMAIL_REGEX = Pattern
            .compile(
                "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08" +
                        "\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x" +
                        "7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\" +
                        "[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][" +
                        "0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|" +
                        "\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])",
                Pattern.CASE_INSENSITIVE
            )
    }

    private val userService by di.instance<UserService>()

    override fun validation(): ValidationContext.(Signup) -> Unit = {
        notMatching("repeatedEmail", it.email, it.repeatedEmail)
        notMatching("repeatedPassword", it.password, it.repeatedPassword)

        minLength("nickname", it.nickname, Constants.MIN_NICKNAME_LENGTH)
        maxLength("nickname", it.nickname, Constants.MAX_NICKNAME_LENGTH)

        minLength("password", it.password, Constants.MIN_PASSWORD_LENGTH)

        maxLength("email", it.email, Constants.MAX_EMAIL_LENGTH)
        maxLength("name", it.name, Constants.MAX_NAME_LENGTH)
        maxLength("surname", it.surname, Constants.MAX_SURNAME_LENGTH)

        it.profileImgUri?.let { e -> maxLength("profileImgUri", e, Constants.MAX_PROFILE_IMG_URI_LENGTH) }

        format("email", !isValidEmail(it.email))
        existing("email", userService.findByEmail(it.email) != null)
        existing("nickname", userService.findByEmail(it.nickname) != null)
    }

    private fun isValidEmail(email: String) = EMAIL_REGEX.matcher(email).find()
}