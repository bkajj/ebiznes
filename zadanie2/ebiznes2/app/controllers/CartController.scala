package controllers

import models.CartItem
import play.api.libs.json._
import play.api.mvc._
import repositories.CartRepository

import javax.inject._
import scala.concurrent.ExecutionContext

@Singleton
class CartController @Inject()(cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getAll: Action[AnyContent] = Action {
    Ok(Json.toJson(CartRepository.getAll))
  }

  def getById(id: Long): Action[AnyContent] = Action {
    CartRepository.getById(id) match {
      case Some(cartItem) => Ok(Json.toJson(cartItem))
      case None           => NotFound(Json.obj("error" -> "Cart item not found"))
    }
  }

  def addToCart: Action[JsValue] = Action(parse.json) { request =>
    request.body.validate[CartItem].fold(
      _ => BadRequest(Json.obj("error" -> "Invalid JSON")),
      cartItem => {
        val newCartItem = CartRepository.add(cartItem)
        Created(Json.toJson(newCartItem))
      }
    )
  }

  def update(id: Long): Action[JsValue] = Action(parse.json) { request =>
    request.body.validate[CartItem].fold(
      _ => BadRequest(Json.obj("error" -> "Invalid JSON")),
      cartItem => {
        if (CartRepository.update(cartItem)) Ok(Json.toJson(cartItem))
        else NotFound(Json.obj("error" -> "Cart item not found"))
      }
    )
  }

  def removeFromCart(id: Long): Action[AnyContent] = Action {
    if (CartRepository.delete(id)) NoContent
    else NotFound(Json.obj("error" -> "Cart item not found"))
  }
}
