package com.hvuitsme.admin.data.model

data class Order(
    var id: String = "",
    val userId: String = "",
    val paymentMethod: String = "",
    val subtotal: Double = 0.0,
    val shipping: Double = 0.0,
    val total: Double = 0.0,
    val timestamp: Long = 0L,
    var status: String = ""
)