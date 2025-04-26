package com.hvuitsme.banzashoes.data.repository

import com.hvuitsme.banzashoes.data.model.Address
import com.hvuitsme.banzashoes.data.model.Order
import com.hvuitsme.banzashoes.data.remote.OrderDataSource

class OrderRepoImpl(
    private val dataSource: OrderDataSource
): OrderRepo {
    override fun observeOrders(userId: String, onDataChange: (List<Order>) -> Unit) {
        dataSource.observeOrdersByUser(userId, onDataChange)
    }
    override suspend fun getOrders(userId: String): List<Order> {
        return dataSource.getOrdersByUser(userId)
    }

    override suspend fun updateStatus(orderId: String, newStatus: String) {
        return dataSource.updateStatus(orderId, newStatus)
    }

    override suspend fun updateAddress(orderId: String, address: Address) {
        return dataSource.updateAddress(orderId, address)
    }
}