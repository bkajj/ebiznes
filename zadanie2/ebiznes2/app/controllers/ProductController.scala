package controllers

import models.Product
import play.api.libs.json._
import play.api.mvc._
import repositories.ProductRepository

import javax.inject._
import scala.concurrent.ExecutionContext

@Singleton
class ProductController @Inject()(cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getAll: Action[AnyContent] = Action {
    Ok(Json.toJson(ProductRepository.getAll))
  }

  def getById(id: Long): Action[AnyContent] = Action {
    ProductRepository.getById(id) match {
      case Some(product) => Ok(Json.toJson(product))
      case None          => NotFound(Json.obj("error" -> "Product not found"))
    }
  }

  def create: Action[JsValue] = Action(parse.json) { request =>
    request.body.validate[Product].fold(
      _ => BadRequest(Json.obj("error" -> "Invalid JSON")),
      product => {
        val newProduct = ProductRepository.add(product)
        Created(Json.toJson(newProduct))
      }
    )
  }

  def update(id: Long): Action[JsValue] = Action(parse.json) { request =>
    request.body.validate[Product].fold(
      _ => BadRequest(Json.obj("error" -> "Invalid JSON")),
      product => {
        if (ProductRepository.update(product)) Ok(Json.toJson(product))
        else NotFound(Json.obj("error" -> "Product not found"))
      }
    )
  }

  def delete(id: Long): Action[AnyContent] = Action {
    if (ProductRepository.delete(id)) NoContent
    else NotFound(Json.obj("error" -> "Product not found"))
  }
}
