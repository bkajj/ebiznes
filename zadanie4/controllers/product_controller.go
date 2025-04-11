package controllers

import (
	"go-echo-app/models"
	"net/http"

	"github.com/labstack/echo/v4"
	"gorm.io/gorm"
)

func GetProducts(db *gorm.DB) echo.HandlerFunc {
	return func(c echo.Context) error {
		var products []models.Product

		if err := db.Preload("Category").Preload("Cart").Preload("Supplier").Find(&products).Error; err != nil {
			return c.JSON(http.StatusInternalServerError, echo.Map{"error": "Error fetching products"})
		}
		return c.JSON(http.StatusOK, products)
	}
}

func CreateProduct(db *gorm.DB) echo.HandlerFunc {
	return func(c echo.Context) error {
		product := new(models.Product)
		if err := c.Bind(product); err != nil {
			return err
		}

		var supplier models.Supplier
		if err := db.First(&supplier, product.SupplierID).Error; err != nil {
			return c.JSON(http.StatusNotFound, echo.Map{"error": "Supplier not found"})
		}

		var category models.Category
		if err := db.First(&category, product.CategoryID).Error; err != nil {
			return c.JSON(http.StatusNotFound, echo.Map{"error": "Category not found"})
		}

		product.Supplier = supplier
		product.Category = category

		db.Create(&product)
		return c.JSON(http.StatusCreated, product)
	}
}

func UpdateProduct(db *gorm.DB) echo.HandlerFunc {
	return func(c echo.Context) error {
		id := c.Param("id")
		var product models.Product
		if err := db.First(&product, id).Error; err != nil {
			return c.JSON(http.StatusNotFound, echo.Map{"error": "Product not found"})
		}

		if err := c.Bind(&product); err != nil {
			return err
		}

		var supplier models.Supplier
		if err := db.First(&supplier, product.SupplierID).Error; err != nil {
			return c.JSON(http.StatusNotFound, echo.Map{"error": "Supplier not found"})
		}

		var category models.Category
		if err := db.First(&category, product.CategoryID).Error; err != nil {
			return c.JSON(http.StatusNotFound, echo.Map{"error": "Category not found"})
		}

		product.Supplier = supplier
		product.Category = category

		db.Save(&product)
		return c.JSON(http.StatusOK, product)
	}
}

func DeleteProduct(db *gorm.DB) echo.HandlerFunc {
	return func(c echo.Context) error {
		id := c.Param("id")
		if err := db.Delete(&models.Product{}, id).Error; err != nil {
			return err
		}
		return c.NoContent(http.StatusNoContent)
	}
}
