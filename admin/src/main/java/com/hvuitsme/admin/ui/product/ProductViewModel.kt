package com.hvuitsme.admin.ui.product

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hvuitsme.admin.data.model.Product
import com.hvuitsme.admin.data.repository.CategoryRepo
import com.hvuitsme.admin.data.repository.ProductRepo
import kotlinx.coroutines.launch

class ProductViewModel(
    private val repository: ProductRepo,
    private val categoryRepo: CategoryRepo
) : ViewModel() {
    val products = MutableLiveData<List<Product>>()
    val brandList = MutableLiveData<List<String>>()
    val productSaved = MutableLiveData<Boolean>()

    fun loadProducts() {
        viewModelScope.launch {
            val data = repository.getProducts()
            products.value = data
        }
    }

    fun loadBrands(){
        viewModelScope.launch {
            try {
                val category = categoryRepo.getCategories()
                brandList.value = category.map { it.cateId.replaceFirstChar { it.uppercase() } }
            } catch (e: Exception){
                brandList.value = emptyList()
            }
        }
    }

    fun saveProduct(product: Product) {
        viewModelScope.launch {
            try {
                if (product.id.isNotEmpty() && product.id != "0"){
                    repository.updateProduct(product.id, product)
                    loadProducts()
                }else{
                    repository.addProduct(product)
                    loadProducts()
                }
                productSaved.value = true
            } catch (e: Exception) {
                productSaved.value = false
            }
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            try {
                repository.deleteProduct(product)
                loadProducts()
            } catch (e: Exception) {
            }
        }
    }
}