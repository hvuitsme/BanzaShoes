package com.hvuitsme.banzashoes.ui.cart

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hvuitsme.banzashoes.data.model.CartDisplayItem
import com.hvuitsme.banzashoes.data.repository.CartRepo
import kotlinx.coroutines.launch

class CartViewModel(
    private val repository: CartRepo
) : ViewModel() {
    val cartDisplayItems = MutableLiveData<List<CartDisplayItem>>()

    fun loadCartItems() {
        viewModelScope.launch {
            cartDisplayItems.value = repository.getCartDisplayItems()
        }
    }

    fun addOrUpdateCartItem(productId: String, qtyToAdd: Int = 1, size: String) {
        viewModelScope.launch {
            if (repository.addOrUpdateCartItem(productId, qtyToAdd, size)) {
                loadCartItems()
            }
        }
    }

    fun updateCartItemQty(productId: String, newQty: Int) {
        val currentItems = cartDisplayItems.value?.toMutableList() ?: return
        val index = currentItems.indexOfFirst { it.productId == productId }
        if (index != -1) {
            if (newQty == 0) {
                currentItems.removeAt(index)
            } else {
                currentItems[index] = currentItems[index].copy(quantity = newQty)
            }
            cartDisplayItems.value = currentItems
        }

        viewModelScope.launch {
            val success = repository.updateCartItemQty(productId, newQty)
            if (!success) {
                loadCartItems()
            }
        }
    }
}