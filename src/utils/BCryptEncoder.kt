package com.gmail.marcosav2010.utils

import at.favre.lib.crypto.bcrypt.BCrypt

object BCryptEncoder {

    fun encode(value: String): String = BCrypt.withDefaults().hashToString(12, value.toCharArray())

    fun verify(value: String, hashed: String) = BCrypt.verifyer().verify(value.toCharArray(), hashed).verified
}