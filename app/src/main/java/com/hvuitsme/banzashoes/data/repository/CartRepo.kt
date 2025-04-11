package com.hvuitsme.banzashoes.data.repository

import com.hvuitsme.banzashoes.data.model.CartDisplayItem

interface CartRepo {
    suspend fun addOrUpdateCartItem(productId: String, qtyToAdd: Int, size: String): Boolean
    suspend fun getCartDisplayItems(): List<CartDisplayItem>
    suspend fun updateCartItemQty(productId: String, newQty: Int): Boolean
    suspend fun clearCart(): Boolean
}