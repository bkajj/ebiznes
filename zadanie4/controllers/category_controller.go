package controllers

import (
	"go-echo-app/models"
	"net/http"

	"github.com/labstack/echo/v4"
	"gorm.io/gorm"
)

func GetCategories(db *gorm.DB) echo.HandlerFunc {
	return func(c echo.Context) error {
		var categories []models.Category
		db.Preload("Products").Find(&categories)
		return c.JSON(http.StatusOK, categories)
	}
}

func CreateCategory(db *gorm.DB) echo.HandlerFunc {
	return func(c echo.Context) error {
		category := new(models.Category)
		if err := c.Bind(category); err != nil {
			return err
		}
		db.Create(&category)
		return c.JSON(http.StatusCreated, category)
	}
}

func UpdateCategory(db *gorm.DB) echo.HandlerFunc {
	return func(c echo.Context) error {
		id := c.Param("id")
		var category models.Category
		if err := db.First(&category, id).Error; err != nil {
			return c.JSON(http.StatusNotFound, echo.Map{"error": "Category not found"})
		}
		if err := c.Bind(&category); err != nil {
			return err
		}
		db.Save(&category)
		return c.JSON(http.StatusOK, category)
	}
}

func DeleteCategory(db *gorm.DB) echo.HandlerFunc {
	return func(c echo.Context) error {
		id := c.Param("id")
		if err := db.Delete(&models.Category{}, id).Error; err != nil {
			return err
		}
		return c.NoContent(http.StatusNoContent)
	}
}
