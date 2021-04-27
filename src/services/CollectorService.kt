package com.gmail.marcosav2010.services

import com.gmail.marcosav2010.api.CollectorAPI
import com.gmail.marcosav2010.model.Session
import kotlinx.coroutines.runBlocking
import org.kodein.di.DI
import org.kodein.di.instance

class CollectorService(di: DI) {

    private val collector by di.instance<CollectorAPI>()

    fun collect(session: Session, item: Long, action: ActionType, value: Double? = null) = runBlocking {
        collector.collect(
            session.sessionId,
            session.userId!!,
            item,
            action.id,
            value
        )
    }
}

enum class ActionType(val id: Int) {
    FAVORITE(1),
    CART(2),
    BUY(3),
    RATING(4),
}