package com.hvuitsme.banzashoes.data.repository

import com.hvuitsme.banzashoes.data.model.Cart
import com.hvuitsme.banzashoes.data.model.CartDisplayItem
import com.hvuitsme.banzashoes.data.remote.CartDataSource
import com.hvuitsme.banzashoes.data.remote.FirebaseDataSource
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class CartRepoImpl(
    private val cartDataSource: CartDataSource,
    private val firebaseDataSource: FirebaseDataSource
) : CartRepo {
    override suspend fun addOrUpdateCartItem(
        productId: String,
        qtyToAdd: Int,
        size: String
    ): Boolean {
        return cartDataSource.addOrUpdateCartItem(productId, qtyToAdd, size)
    }

    override suspend fun getCartDisplayItems(): List<CartDisplayItem> {
        val cartItem: List<Cart> = cartDataSource.getCartItems()
        val displayList = mutableListOf<CartDisplayItem>()
        coroutineScope {
            val deferreds = cartItem.map { item ->
                async {
                    val product = firebaseDataSource.getProductsById(item.productId)
                    product?.let {
                        CartDisplayItem(
                            productId = product.id,
                            title = product.title,
                            imageUrls = product.imageUrls.firstOrNull() ?: "",
                            price = product.price,
                            quantity = item.qty,
                            size = item.size
                        )
                    }
                }
            }
            deferreds.forEach { deferred ->
                deferred.await()?.let { displayItem ->
                    displayList.add(displayItem)
                }
            }
        }
        return displayList
    }

    override suspend fun updateCartItemQty(productId: String, newQty: Int): Boolean {
        return cartDataSource.updateCartItemQty(productId, newQty)
    }

}