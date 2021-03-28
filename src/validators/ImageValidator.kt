package com.gmail.marcosav2010.validators

import com.gmail.marcosav2010.Constants
import java.io.File

class ImageValidator : Validator<String>() {

    override fun validation(): ValidationContext.(String) -> Unit = {
        check("image", File(it).extension !in Constants.ALLOWED_IMAGE_EXT, "invalid_img_format")
    }

    fun onOversize(): Nothing = throw ValidationException(mapOf("image" to "max_size.${Constants.MAX_IMAGE_MB_SIZE}"))
}