package com.hvuitsme.banzashoes.ui.cart

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hvuitsme.banzashoes.data.model.CartDisplayItem
import com.hvuitsme.banzashoes.data.repository.CartRepo
import kotlinx.coroutines.launch

class CartViewModel(
    private val repository: CartRepo
): ViewModel() {
    val cartDisplayItems = MutableLiveData<List<CartDisplayItem>>()

    fun loadCartItems(){
        viewModelScope.launch {
            cartDisplayItems.value = repository.getCartDisplayItems()
        }
    }

    fun addOrUpdateCartItem(productId: String, qtyToAdd: Int = 1){
        viewModelScope.launch {
            if (repository.addOrUpdateCartItem(productId, qtyToAdd)){
                loadCartItems()
            }
        }
    }

    fun updateCartItemQty(productId: String, newQty: Int) {
        val currentList = cartDisplayItems.value?.toMutableList() ?: mutableListOf()
        val index = currentList.indexOfFirst { it.productId == productId }
        if (index != -1) {
            val oldItem = currentList[index]
            val updatedItem = oldItem.copy(quantity = newQty)
            currentList[index] = updatedItem
            cartDisplayItems.value = currentList
        }

        viewModelScope.launch {
            val success = repository.updateCartItemQty(productId, newQty)
            if (!success) {
                loadCartItems()
            }
        }
    }
}