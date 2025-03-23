package com.hvuitsme.banzashoes.data.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hvuitsme.banzashoes.data.model.Cart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CartDataSource {
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun addOrUpdateCartItem(productId: String, qtyToAdd: Int, size: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val uid = auth.currentUser?.uid ?: return@withContext false
                val ref = database.getReference("Cart").child(uid)
                val key = "${productId}_$size"
                val cartRef = ref.child(key)
                val snapshot = cartRef.get().await()
                if (snapshot.exists()) {
                    val currentQty = snapshot.child("qty").getValue(Int::class.java) ?: 0
                    val newQty = currentQty + qtyToAdd
                    if (newQty <= 0) {
                        cartRef.removeValue().await()
                    } else {
                        cartRef.child("qty").setValue(newQty).await()
                    }
                } else {
                    // Tạo mục mới với thông tin productId, qty và size
                    val newItem = mapOf(
                        "productId" to productId,
                        "qty" to qtyToAdd,
                        "size" to size
                    )
                    cartRef.setValue(newItem).await()
                }
                true
            } catch (e: Exception) {
                Log.e("CartDataSource", "Error adding cart item: ${e.message}")
                false
            }
        }

    suspend fun getCartItems(): List<Cart> = withContext(Dispatchers.IO) {
        try {
            val uid = auth.currentUser?.uid ?: return@withContext emptyList()
            val snapshot = database.getReference("Cart").child(uid).get().await()
            snapshot.children.mapNotNull { it.getValue(Cart::class.java) }
        } catch (e: Exception) {
            Log.e("CartDataSource", "Error fetching cart items: ${e.message}")
            emptyList()
        }
    }

    suspend fun updateCartItemQty(productId: String, newQty: Int): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val uid = auth.currentUser?.uid ?: return@withContext false
                val ref = database.getReference("Cart").child(uid)
                val snapshot = ref.orderByChild("productId").equalTo(productId).get().await()
                val cartKey = snapshot.children.firstOrNull()?.key
                if (cartKey != null) {
                    if (newQty <= 0) {
                        ref.child(cartKey).removeValue().await()
                    } else {
                        ref.child(cartKey).child("qty").setValue(newQty).await()
                    }
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                Log.e("CartDataSource", "Error updating quantity: ${e.message}")
                false
            }
        }
}