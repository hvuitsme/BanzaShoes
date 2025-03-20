package com.hvuitsme.banzashoes.data.repository

import com.hvuitsme.banzashoes.data.model.Cart
import com.hvuitsme.banzashoes.data.model.CartDisplayItem
import com.hvuitsme.banzashoes.data.remote.CartDataSource
import com.hvuitsme.banzashoes.data.remote.FirebaseDataSource

class CartRepoImpl(
    private val cartDataSource: CartDataSource,
    private val firebaseDataSource: FirebaseDataSource
): CartRepo {
    override suspend fun addOrUpdateCartItem(productId: String, qtyToAdd: Int): Boolean {
        return cartDataSource.addOrUpdateCartItem(productId, qtyToAdd)
    }

    override suspend fun getCartDisplayItems(): List<CartDisplayItem> {
        val cartItem: List<Cart> = cartDataSource.getCartItems()
        val displayList = mutableListOf<CartDisplayItem>()
        for (item in cartItem){
            val product = firebaseDataSource.getProductsById(item.productId)
            if (product != null){
                displayList.add(
                    CartDisplayItem(
                        productId = product.id,
                        title = product.title,
                        imageUrls = product.imageUrls.firstOrNull() ?: "",
                        price = product.price,
                        quantity = item.qty
                    )
                )
            }
        }
        return displayList
    }

    override suspend fun updateCartItemQty(productId: String, newQty: Int): Boolean {
        return cartDataSource.updateCartItemQty(productId, newQty)
    }

}