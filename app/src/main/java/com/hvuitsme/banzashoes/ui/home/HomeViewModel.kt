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
    private val repository: BanzaRepo,
    private val state: SavedStateHandle
) : ViewModel() {

    val carousel = MutableLiveData<List<Carousel>>()
    val category = MutableLiveData<List<Category>>()
    val product = MutableLiveData<List<Product>>()

    val searchResult = MutableLiveData<List<Product>>()
    val selectedCategoryPosition = state.getLiveData("SELECTED_CATEGORY_POSITION", 0)

    fun loadCarousel(force: Boolean = false) {
        if (!force && !carousel.value.isNullOrEmpty()) return
        viewModelScope.launch {
            val data = repository.getCarousel()
            carousel.value = data
        }
    }

    fun loadCategory(force: Boolean = false) {
        if (!force && !category.value.isNullOrEmpty()) return
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

    fun searchProduct(query: String) {
        viewModelScope.launch {
            if (query.isBlank()){
                searchResult.value = emptyList()
            }else{
                val allProduct = repository.getAllProducts()
                val filltered = allProduct.filter {
                    it.title.contains(query, ignoreCase = true) ||
                    it.brand.contains(query, ignoreCase = true)
                }
                searchResult.value = filltered
            }
        }
    }
}