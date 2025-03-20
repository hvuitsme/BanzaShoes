package com.hvuitsme.banzashoes.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hvuitsme.banzashoes.data.model.Carousel
import com.hvuitsme.banzashoes.data.model.Category
import com.hvuitsme.banzashoes.data.model.Product
import com.hvuitsme.banzashoes.data.repository.BanzaRepo
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: BanzaRepo
) : ViewModel() {

    val carousel = MutableLiveData<List<Carousel>>()
    val category = MutableLiveData<List<Category>>()
    val product = MutableLiveData<List<Product>>()

    fun loadCarousel() {
        if (!carousel.value.isNullOrEmpty()) return
        viewModelScope.launch {
            val data = repository.getCarousel()
            carousel.value = data
        }
    }

    fun loadCategory() {
        if (!category.value.isNullOrEmpty()) return
        viewModelScope.launch {
            val data = repository.getCategories()
            category.value = data
        }
    }

    fun loadProduct(selectedCateId: String = "") {
        viewModelScope.launch {
            val data = repository.getProductsByCategory(selectedCateId)
            product.value = data
        }
    }
}