package repositories

import models.Category
import scala.collection.mutable

object CategoryRepository {
  private val categories = mutable.ListBuffer(
    Category(1, "Elektronika"),
    Category(2, "AGD")
  )

  def getAll: Seq[Category] = categories.toList
  def getById(id: Long): Option[Category] = categories.find(_.id == id)
  def add(category: Category): Category = {
    categories += category
    category
  }
  def update(updatedCategory: Category): Boolean = {
    getById(updatedCategory.id) match {
      case Some(c) =>
        categories -= c
        categories += updatedCategory
        true
      case None => false
    }
  }
  def delete(id: Long): Boolean = {
    getById(id).exists { c =>
      categories -= c
      true
    }
  }
}
