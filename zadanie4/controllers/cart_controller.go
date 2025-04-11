package controllers

import (
	"go-echo-app/models"
	"net/http"

	"github.com/labstack/echo/v4"
	"gorm.io/gorm"
)

func GetCarts(db *gorm.DB) echo.HandlerFunc {
	return func(c echo.Context) error {
		var carts []models.Cart
		db.Preload("Products").Find(&carts)
		return c.JSON(http.StatusOK, carts)
	}
}

func CreateCart(db *gorm.DB) echo.HandlerFunc {
	return func(c echo.Context) error {
		cart := new(models.Cart)
		if err := c.Bind(cart); err != nil {
			return err
		}
		db.Create(&cart)
		return c.JSON(http.StatusCreated, cart)
	}
}

func UpdateCart(db *gorm.DB) echo.HandlerFunc {
	return func(c echo.Context) error {
		id := c.Param("id")
		var cart models.Cart
		if err := db.First(&cart, id).Error; err != nil {
			return c.JSON(http.StatusNotFound, echo.Map{"error": "Cart not found"})
		}
		if err := c.Bind(&cart); err != nil {
			return err
		}
		db.Save(&cart)
		return c.JSON(http.StatusOK, cart)
	}
}

func DeleteCart(db *gorm.DB) echo.HandlerFunc {
	return func(c echo.Context) error {
		id := c.Param("id")
		if err := db.Delete(&models.Cart{}, id).Error; err != nil {
			return err
		}
		return c.NoContent(http.StatusNoContent)
	}
}
