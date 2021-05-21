package com.gmail.marcosav2010.utils

fun String.dropFrom(n: Int) = substring(0 until kotlin.math.min(length, n))

infix fun ClosedRange<Double>.step(step: Double): Iterable<Double> {
    require(start.isFinite())
    require(endInclusive.isFinite())
    require(step > 0.0) { "Step must be positive, was: $step." }

    val sequence = generateSequence(start) { previous ->
        if (previous == Double.POSITIVE_INFINITY) return@generateSequence null
        val next = previous + step
        if (next > endInclusive) null else next
    }

    return sequence.asIterable()
}

inline fun <R> runSilent(default: R, block: () -> R): R =
    runCatching { block() }.onFailure {
        if (it.message?.startsWith("Failed to connect") != true) it.printStackTrace(
            System.err
        )
    }.getOrDefault(default)

inline fun <R> runSilent(block: () -> R) {
    runCatching { block() }.onFailure {
        if (it.message?.startsWith("Failed to connect") != true) it.printStackTrace(
            System.err
        )
    }
}