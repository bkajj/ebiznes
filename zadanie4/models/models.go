package models

import "gorm.io/gorm"

type Product struct {
	gorm.Model
	Nazwa      string
	Price      float64
	CartID     uint
	Cart       Cart
	CategoryID uint
	Category   Category
	SupplierID uint
	Supplier   Supplier
}

type Category struct {
	gorm.Model
	Name     string
	Products []Product
}

type Cart struct {
	gorm.Model
	UserID   uint
	User     User
	Products []Product
}

type User struct {
	gorm.Model
	Name  string
	Email string
	Carts []Cart
}

type Supplier struct {
	gorm.Model
	Name        string
	PhoneNumber string
	Products    []Product
}
