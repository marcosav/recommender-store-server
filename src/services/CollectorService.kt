package com.gmail.marcosav2010.services

import com.gmail.marcosav2010.api.CollectorAPI
import com.gmail.marcosav2010.model.Session
import kotlinx.coroutines.runBlocking
import org.kodein.di.DI
import org.kodein.di.instance

class CollectorService(di: DI) {

    private val collector by di.instance<CollectorAPI>()

    fun collectRating(session: Session, item: Long, value: Double) = runBlocking {
        collector.collect(
            session.sessionId, session.userId!!, item, ActionType.RATING.id, value
        )
    }

    fun collectCart(session: Session, item: Long) = runBlocking {
        collector.collect(
            session.sessionId, session.userId!!, item, ActionType.CART.id,
        )
    }

    fun collectFavorite(session: Session, item: Long) = runBlocking {
        collector.collect(
            session.sessionId, session.userId!!, item, ActionType.FAVORITE.id,
        )
    }

    fun collectBuy(session: Session, item: List<Long>) = runBlocking {
        item.forEach {
            collector.collect(session.sessionId, session.userId!!, it, ActionType.BUY.id)
        }
    }
}

enum class ActionType(val id: Int) {
    FAVORITE(1),
    CART(2),
    BUY(3),
    RATING(4),
}