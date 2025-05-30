package com.hvuitsme.banzashoes.data.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hvuitsme.banzashoes.data.model.Address
import com.hvuitsme.banzashoes.data.model.Order
import com.hvuitsme.banzashoes.data.model.Review
import kotlinx.coroutines.tasks.await

class OrderDataSource {
    private val database = FirebaseDatabase.getInstance()
    private val ordersRef = database.getReference("Orders")
    private val productsRef = database.getReference("Product")

    fun observeOrdersByUser(userId: String, onDataChange: (List<Order>) -> Unit) {
        ordersRef.orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Order>()
                    snapshot.children.forEach { child ->
                        child.getValue(Order::class.java)?.let { list.add(it) }
                    }
                    onDataChange(list)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    suspend fun getOrdersByUser(userId: String): List<Order> {
        return try {
            val snapshot = ordersRef.orderByChild("userId").equalTo(userId).get().await()
            val list = mutableListOf<Order>()
            snapshot.children.forEach { child ->
                child.getValue(Order::class.java)?.let { list.add(it) }
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun updateStatus(orderId: String, newStatus: String) {
        ordersRef.child(orderId).child("status").setValue(newStatus).await()
    }

    suspend fun updateAddress(orderId: String, address: Address) {
        ordersRef.child(orderId).child("address").setValue(address).await()
    }

    suspend fun addProductReview(productId: String, review: Review) {
        val key = productsRef.child(productId).child("reviews").push().key ?: return
        productsRef.child(productId).child("reviews").child(key).setValue(review).await()
    }

    suspend fun markOrderReviewed(orderId: String) {
        ordersRef.child(orderId).updateChildren(mapOf("reviewed" to true)).await()
    }
}