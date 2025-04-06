package com.hvuitsme.admin.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hvuitsme.admin.data.repository.CategoryRepo
import com.hvuitsme.admin.data.repository.ProductRepo

@Suppress("UNCHECKED_CAST")
class ProductViewModelFactory(
    private val repository: ProductRepo,
    private val categoryRepo: CategoryRepo
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)){
            return ProductViewModel(repository, categoryRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}