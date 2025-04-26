package com.hvuitsme.banzashoes.data.remote

import com.google.firebase.database.FirebaseDatabase
import com.hvuitsme.banzashoes.data.model.Order
import kotlinx.coroutines.tasks.await

class CheckoutDataSource {
    private val database = FirebaseDatabase.getInstance()
    private val ref = database.getReference("Orders")

    suspend fun createOrder(order: Order): Boolean {
        return try {
            val key = ref.push().key ?: return false
            order.id = key
            ref.child(key).setValue(order).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}