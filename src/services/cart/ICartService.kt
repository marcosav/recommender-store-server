package com.gmail.marcosav2010.services.cart

import com.gmail.marcosav2010.model.CartProductPreview
import com.gmail.marcosav2010.model.Session
import com.gmail.marcosav2010.model.SessionCart

interface ICartService {

    fun updateProductAmount(session: Session, productId: Long, amount: Int, add: Boolean): SessionCart

    fun remove(session: Session, productId: Long): SessionCart

    fun clear(session: Session): SessionCart

    fun getCurrentCart(session: Session): List<CartProductPreview>
}