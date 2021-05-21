package com.gmail.marcosav2010.services

import com.gmail.marcosav2010.api.CollectorAPI
import com.gmail.marcosav2010.model.Session
import com.gmail.marcosav2010.utils.runSilent
import kotlinx.coroutines.runBlocking
import org.kodein.di.DI
import org.kodein.di.instance

class CollectorService(di: DI) {

    private val collector by di.instance<CollectorAPI>()

    suspend fun collectRating(session: Session, item: Long, value: Double) =
        collect(session, item, ActionType.RATING, value)

    suspend fun collectCart(session: Session, item: Long) =
        collect(session, item, ActionType.CART)

    suspend fun collectFavorite(session: Session, item: Long) = collect(session, item, ActionType.FAVORITE)

    suspend fun collectBuy(session: Session, item: List<Long>) = runSilent {
        item.forEach { collector.collect(session.sessionId, session.userId!!, it, ActionType.BUY.id) }
    }

    private suspend fun collect(
        session: Session,
        item: Long,
        action: ActionType,
        value: Double? = null
    ): Unit = runSilent { session.userId?.let { collector.collect(session.sessionId, it, item, action.id, value) } }
}

enum class ActionType(val id: Int) {
    FAVORITE(1),
    CART(2),
    BUY(3),
    RATING(4),
}