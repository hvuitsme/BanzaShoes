package com.hvuitsme.banzashoes.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hvuitsme.banzashoes.data.repository.CartRepo

@Suppress("UNCHECKED_CAST")
class CartViewModelFactory(
    private val repository: CartRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(repository) as T
        }
        throw IllegalArgumentException("Unkown ViewModel class")
    }
}