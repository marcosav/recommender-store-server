package com.gmail.marcosav2010.validators

typealias ValidationResult = Map<String, String>

abstract class Validator<T> {

    fun validate(input: T) {
        val ctx = ValidationContext()
        validation()(ctx, input)
        if (ctx.hasErrors())
            throw ValidationException(ctx.result)
    }

    protected abstract fun validation(): ValidationContext.(T) -> Unit
}

class ValidationContext {

    private val errors = mutableMapOf<String, String>()

    fun hasErrors() = errors.isNotEmpty()

    val result: ValidationResult = errors

    fun maxLength(field: String, current: String, max: Int) =
        check(field, current.length > max, "max_length", max.toString())

    fun minLength(field: String, current: String, min: Int) =
        check(field, current.length < min, "min_length", min.toString())

    fun mandatory(field: String, current: String) =
        check(field, current.isBlank(), "mandatory")

    fun format(field: String, condition: Boolean) =
        check(field, condition, "invalid_format")

    fun existing(field: String, condition: Boolean) =
        check(field, condition, "already_exists")

    fun notMatching(field: String, value1: Comparable<*>, value2: Comparable<*>) =
        check(field, value1 != value2, "not_matching")

    fun <T : Number> max(field: String, current: Comparable<T>, max: T) =
        check(field, current > max, "max", max.toString())

    fun <T : Number> min(field: String, current: Comparable<T>, min: T) =
        check(field, current < min, "min", min.toString())

    fun check(field: String, condition: Boolean, vararg tags: String) {
        if (condition && !errors.containsKey(field)) errors[field] = tags.joinToString(".")
    }
}