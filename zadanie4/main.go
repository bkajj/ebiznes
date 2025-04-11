package main

import (
	"go-echo-app/controllers"
	"go-echo-app/models"
	"log"

	"github.com/labstack/echo/v4"
	"gorm.io/driver/sqlite"
	"gorm.io/gorm"
)

func seedDatabase(db *gorm.DB) {
	elektronika := models.Category{Name: "Elektronika"}
	ksiazki := models.Category{Name: "Książki"}
	ubrania := models.Category{Name: "Ubrania"}
	db.Create(&elektronika)
	db.Create(&ksiazki)
	db.Create(&ubrania)

	jan := models.User{Name: "Jan Kowalski", Email: "jan.kowalski@example.com"}
	anna := models.User{Name: "Anna Nowak", Email: "anna.nowak@example.com"}
	db.Create(&jan)
	db.Create(&anna)

	koszyk1 := models.Cart{UserID: jan.ID}
	koszyk2 := models.Cart{UserID: anna.ID}
	db.Create(&koszyk1)
	db.Create(&koszyk2)

	importix := models.Supplier{Name: "Importix", PhoneNumber: "123456789"}
	polKomDot := models.Supplier{Name: "PolKomDot", PhoneNumber: "987654321"}
	db.Create(&importix)
	db.Create(&polKomDot)

	produkty := []models.Product{
		{Nazwa: "Xiaomi Redmi Note 14", Price: 1299.99, CategoryID: elektronika.ID, CartID: koszyk1.ID, SupplierID: importix.ID},
		{Nazwa: "Lenovo Yoga 9i", Price: 4500.00, CategoryID: elektronika.ID, CartID: koszyk2.ID, SupplierID: polKomDot.ID},
		{Nazwa: "Amazon Kindle", Price: 499.99, CategoryID: elektronika.ID, CartID: koszyk1.ID, SupplierID: importix.ID},
		{Nazwa: "Wiedzmin", Price: 49.99, CategoryID: ksiazki.ID, CartID: koszyk1.ID, SupplierID: polKomDot.ID},
		{Nazwa: "Clean Code", Price: 50.50, CategoryID: ksiazki.ID, CartID: koszyk2.ID, SupplierID: importix.ID},
		{Nazwa: "Bluza Black", Price: 89.99, CategoryID: ubrania.ID, CartID: koszyk1.ID, SupplierID: polKomDot.ID},
		{Nazwa: "Koszulka Polo", Price: 99.99, CategoryID: ubrania.ID, CartID: koszyk2.ID, SupplierID: importix.ID},
	}

	for _, p := range produkty {
		db.Create(&p)
	}

}
func main() {
	db, err := gorm.Open(sqlite.Open("baza_danych.db"), &gorm.Config{})
	if err != nil {
		log.Fatal("Error: ", err)
	}

	e := echo.New()
	db.AutoMigrate(&models.Product{}, &models.Category{}, &models.Cart{}, &models.User{}, &models.Supplier{})
	seedDatabase(db)

	e.GET("/products", controllers.GetProducts(db))
	e.POST("/products", controllers.CreateProduct(db))
	e.PUT("/products/:id", controllers.UpdateProduct(db))
	e.DELETE("/products/:id", controllers.DeleteProduct(db))

	e.GET("/categories", controllers.GetCategories(db))
	e.POST("/categories", controllers.CreateCategory(db))
	e.PUT("/categories/:id", controllers.UpdateCategory(db))
	e.DELETE("/categories/:id", controllers.DeleteCategory(db))

	e.GET("/carts", controllers.GetCarts(db))
	e.POST("/carts", controllers.CreateCart(db))
	e.PUT("/carts/:id", controllers.UpdateCart(db))
	e.DELETE("/carts/:id", controllers.DeleteCart(db))

	e.Logger.Fatal(e.Start(":8080"))
}
