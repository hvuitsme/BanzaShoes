package com.hvuitsme.banzashoes.data.repository

import com.hvuitsme.banzashoes.data.model.Address
import com.hvuitsme.banzashoes.data.model.Order

interface OrderRepo {
    fun observeOrders(userId: String, onDataChange: (List<Order>) -> Unit)
    suspend fun getOrders(userId: String): List<Order>
    suspend fun updateStatus(orderId: String, newStatus: String)
    suspend fun updateAddress(orderId: String, address: Address)
}