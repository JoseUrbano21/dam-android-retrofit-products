package com.unirfp.myapplication

// Mapeo de los detalles de producto
data class Product(
    val _id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val image: String,
    val active: Boolean
)