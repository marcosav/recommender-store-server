package com.gmail.marcosav2010.validators

abstract class Validator<T> {

    fun validate(input: T) {
        val ctx = ValidationContext()
        validation()(ctx, input)
        if (ctx.hasErrors())
            throw IllegalArgumentException(ctx.errors.toString())
    }

    protected abstract fun validation(): ValidationContext.(T) -> Unit
}

class ValidationContext {

    val errors = mutableListOf<String>()

    fun hasErrors() = errors.isNotEmpty()

    fun maxLength(field: String, current: String, max: Int) =
        check(field, current.length > max, "max_length", max.toString())

    fun minLength(field: String, current: String, min: Int) =
        check(field, current.length < min, "min_length", min.toString())

    fun format(field: String, condition: Boolean) =
        check(field, condition, "invalid_format")

    fun existing(field: String, condition: Boolean) =
        check(field, condition, "already_exists")

    fun notMatching(field: String, value1: Comparable<*>, value2: Comparable<*>) =
        check(field, value1 != value2, "not_matching")

    private fun check(field: String, condition: Boolean, vararg tags: String) {
        if (condition) errors.add("$field.${tags.joinToString(".")}")
    }
}