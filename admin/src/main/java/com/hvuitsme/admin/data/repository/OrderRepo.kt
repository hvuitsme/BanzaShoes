package com.hvuitsme.admin.data.repository

import com.hvuitsme.admin.data.model.Order

interface OrderRepo{
    suspend fun getOrders(): List<Order>
    suspend fun updateStatus(orderId: String, newStatus: String)
    suspend fun deleteOrder(orderId: String)
}