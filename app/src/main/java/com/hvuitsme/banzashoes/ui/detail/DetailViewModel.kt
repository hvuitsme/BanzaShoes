package com.hvuitsme.banzashoes.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hvuitsme.banzashoes.data.model.Product
import com.hvuitsme.banzashoes.data.repository.BanzaRepo
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: BanzaRepo
) : ViewModel() {
    val product = MutableLiveData<Product?>()

    fun loadDetailProduct(productId: String) {
        viewModelScope.launch {
            val detailProduct = repository.getProductsById(productId)
            product.value = detailProduct
        }
    }
}