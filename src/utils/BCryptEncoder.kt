package com.gmail.marcosav2010.utils

import at.favre.lib.crypto.bcrypt.BCrypt

object BCryptEncoder {

    fun encode(value: String): String = BCrypt.withDefaults().hashToString(12, value.toCharArray())
}