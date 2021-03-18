package com.gmail.marcosav2010.utils

fun String.dropFrom(n: Int) = substring(0 until kotlin.math.min(length, n))