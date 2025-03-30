package controllers

import models.Category
import play.api.libs.json._
import play.api.mvc._
import repositories.CategoryRepository

import javax.inject._
import scala.concurrent.ExecutionContext

@Singleton
class CategoryController @Inject()(cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getAll: Action[AnyContent] = Action {
    Ok(Json.toJson(CategoryRepository.getAll))
  }

  def getById(id: Long): Action[AnyContent] = Action {
    CategoryRepository.getById(id) match {
      case Some(category) => Ok(Json.toJson(category))
      case None          => NotFound(Json.obj("error" -> "Category not found"))
    }
  }

  def create: Action[JsValue] = Action(parse.json) { request =>
    request.body.validate[Category].fold(
      _ => BadRequest(Json.obj("error" -> "Invalid JSON")),
      category => {
        val newCategory = CategoryRepository.add(category)
        Created(Json.toJson(newCategory))
      }
    )
  }

  def update(id: Long): Action[JsValue] = Action(parse.json) { request =>
    request.body.validate[Category].fold(
      _ => BadRequest(Json.obj("error" -> "Invalid JSON")),
      category => {
        if (CategoryRepository.update(category)) Ok(Json.toJson(category))
        else NotFound(Json.obj("error" -> "Category not found"))
      }
    )
  }

  def delete(id: Long): Action[AnyContent] = Action {
    if (CategoryRepository.delete(id)) NoContent
    else NotFound(Json.obj("error" -> "Category not found"))
  }
}
