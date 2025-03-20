package com.hvuitsme.banzashoes.data.model

data class CartDisplayItem(
    val productId: String = "",
    val title: String = "",
    val imageUrls: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
)
