package com.gmail.marcosav2010.services.cart

import com.gmail.marcosav2010.model.CartProductPreview
import com.gmail.marcosav2010.model.Session
import com.gmail.marcosav2010.model.SessionCart
import com.gmail.marcosav2010.services.AuthenticationService
import org.kodein.di.DI
import org.kodein.di.instance

class CartService(di: DI) {

    private val authService by di.instance<AuthenticationService>()

    private val loggedCS = LoggedCartService()
    private val sessionCS = SessionCartService(di)

    fun findByUser(userId: Long): SessionCart = loggedCS.findByUser(userId)

    fun getCurrentCart(session: Session): List<CartProductPreview> = service(session).getCurrentCart(session)

    fun updateProductAmount(session: Session, productId: Long, amount: Long, add: Boolean) =
        service(session).updateProductAmount(session, productId, amount, add).updatedToken(session)

    fun mergeCarts(userId: Long, items: SessionCart) =
        loggedCS.mergeCarts(userId, items)

    fun remove(session: Session, productId: Long) =
        service(session).remove(session, productId).updatedToken(session)

    fun clear(session: Session) = service(session).clear(session).updatedToken(session)

    private fun service(session: Session) = if (session.userId != null) loggedCS else sessionCS

    private fun SessionCart.updatedToken(session: Session) =
        authService.token(session.sessionId, this, session.userId, session.username)
}