package com.hvuitsme.banzashoes.data.model

data class Order(
    var id: String = "",
    val userId: String = "",
    val address: Address = Address(),
    val cartItems: List<CartDisplayItem> = emptyList(),
    val paymentMethod: String = "",
    val subtotal: Double = 0.0,
    val shipping: Double = 0.0,
    val total: Double = 0.0,
    val status: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val reviewed: Boolean = false
)