package repositories

import models.Product

import scala.collection.mutable

object ProductRepository {
  private val products = mutable.ListBuffer(
    Product(1, "Laptop", 3000.0, 1),
    Product(2, "Smartphone", 2000.0, 1),
    Product(3, "Odkurzacz", 300.0, 2),
    Product(4, "Czajnik", 180.0, 2)
  )

  def getAll: Seq[Product] = products.toList
  def getById(id: Long): Option[Product] = products.find(_.id == id)
  def add(product: Product): Product = { products += product; product }
  def update(updatedProduct: Product): Boolean = {
    getById(updatedProduct.id) match {
      case Some(p) =>
        products -= p
        products += updatedProduct
        true
      case None => false
    }
  }
  def delete(id: Long): Boolean = {
    getById(id).exists { p =>
      products -= p
      true
    }
  }
}
