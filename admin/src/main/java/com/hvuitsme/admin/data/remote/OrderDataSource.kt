package com.hvuitsme.admin.data.remote

import com.google.firebase.database.FirebaseDatabase
import com.hvuitsme.admin.data.model.Order
import kotlinx.coroutines.tasks.await

class OrderDataSource {
    private val database = FirebaseDatabase.getInstance()
    private val orderRef = database.getReference("Orders")

    suspend fun getOrders(): List<Order> {
        return try {
            val snapshot = orderRef.get().await()
            val orders = mutableListOf<Order>()
            snapshot.children.forEach { child ->
                val order = child.getValue(Order::class.java)
                order?.let { orders.add(it) }
            }
            orders
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun updateStatus(orderId: String, newStatus: String) {
        orderRef.child(orderId).child("status").setValue(newStatus)
    }

    suspend fun deleteOrder(orderId: String) {
        orderRef.child(orderId).removeValue()
    }
}