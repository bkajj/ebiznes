package repositories

import models.CartItem
import scala.collection.mutable

object CartRepository {
  private val cartItems = mutable.ListBuffer(
    CartItem(1, productId = 1, quantity = 1),
    CartItem(2, productId = 2, quantity = 1)
  )

  def getAll: Seq[CartItem] = cartItems.toList
  def getById(id: Long): Option[CartItem] = cartItems.find(_.id == id)
  def add(cartItem: CartItem): CartItem = {
    cartItems += cartItem
    cartItem
  }
  def update(updatedCartItem: CartItem): Boolean = {
    getById(updatedCartItem.id) match {
      case Some(item) =>
        cartItems -= item
        cartItems += updatedCartItem
        true
      case None => false
    }
  }
  def delete(id: Long): Boolean = {
    getById(id).exists { item =>
      cartItems -= item
      true
    }
  }
}
