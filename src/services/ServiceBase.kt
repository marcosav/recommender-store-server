package com.gmail.marcosav2010.services

import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction

abstract class ServiceBase {

    protected fun <T, R> findPaged(
        query: SizedIterable<T>,
        mapper: (T) -> R,
        size: Int,
        offset: Int,
        vararg order: Pair<Expression<*>, SortOrder>
    ): Pair<List<R>, Long> =
        transaction {
            val total = query.count()

            val products = query.orderBy(*order)
                .limit(size, offset.toLong())
                .map(mapper)

            Pair(products, total)
        }
}