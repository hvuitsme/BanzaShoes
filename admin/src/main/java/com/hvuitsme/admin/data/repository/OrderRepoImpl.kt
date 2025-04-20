package com.hvuitsme.admin.data.repository

import com.hvuitsme.admin.data.model.Order
import com.hvuitsme.admin.data.remote.OrderDataSource

class OrderRepoImpl(
    private val databaseSource: OrderDataSource
): OrderRepo {
    override fun observeOrder(onDataChange: (List<Order>) -> Unit) {
        return databaseSource.observeOrders(onDataChange)
    }

    override suspend fun getOrders(): List<Order> {
        return databaseSource.getOrders()
    }

    override suspend fun updateStatus(orderId: String, newStatus: String) {
        databaseSource.updateStatus(orderId, newStatus)
    }

    override suspend fun deleteOrder(orderId: String) {
        databaseSource.deleteOrder(orderId)
    }
}