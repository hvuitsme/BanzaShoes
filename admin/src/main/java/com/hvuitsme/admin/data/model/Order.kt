package com.hvuitsme.admin.data.model

data class Order(
    val id: String = "",
    val userId: String = "",
    val address: Address = Address(),
    val cartItems: List<OrderItem> = emptyList(),
    val paymentMethod: String = "",
    val shipping: Double = 0.0,
    val status: String = "",
    val subtotal: Double = 0.0,
    val total: Double = 0.0,
    val timestamp: Long = 0L
)