package com.hvuitsme.admin.data.model

data class OrderItem(
    val imageUrls: String = "",
    val price: Double = 0.0,
    val productId: String = "",
    val quantity: Int = 0,
    val size: String = "",
    val title: String = ""
)