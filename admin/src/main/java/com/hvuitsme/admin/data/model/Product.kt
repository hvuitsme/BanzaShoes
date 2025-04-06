package com.hvuitsme.admin.data.model

data class Product(
    val id: String = "",
    val cateId: String = "",
    val title: String = "",
    val brand: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val rating: Double = 0.0,
    val imageUrls: List<String> = emptyList(),
    val sizes: List<Size> = emptyList(),
    val reviews: List<Review> = emptyList()
)