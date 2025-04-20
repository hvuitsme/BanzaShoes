package com.hvuitsme.admin.data.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hvuitsme.admin.data.model.Order
import kotlinx.coroutines.tasks.await

class OrderDataSource {
    private val database = FirebaseDatabase.getInstance()
    private val orderRef = database.getReference("Orders")

    fun observeOrders(onDataChange: (List<Order>) -> Unit) {
        orderRef
            .orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = mutableListOf<Order>()
                snapshot.children.forEach { child ->
                    child.getValue(Order::class.java)?.let { orders.add(it) }
                }
                onDataChange(orders)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

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