package db

import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction


fun <T, R> SizedIterable<T>.paged(
    mapper: (T) -> R,
    size: Int,
    offset: Int,
    vararg order: Pair<Expression<*>, SortOrder>
): Paged<R> {
    if (offset < 0) throw PagingException()

    return transaction {
        val total = count()

        val products = orderBy(*order)
            .limit(size, offset.toLong())
            .map(mapper)

        Paged(products, total, size)
    }
}

class Paged<T>(val items: List<T>, val total: Long, val pageSize: Int)

class PagingException : Exception()