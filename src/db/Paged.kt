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
): Paged<R> =
    transaction {
        val total = count()

        val products = orderBy(*order)
            .limit(size, offset.toLong())
            .map(mapper)

        Paged(products, total)
    }

class Paged<T>(val items: List<T>, val total: Long)