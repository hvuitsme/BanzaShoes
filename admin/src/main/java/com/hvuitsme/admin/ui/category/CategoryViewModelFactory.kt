package com.hvuitsme.admin.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hvuitsme.admin.data.repository.CategoryRepo

@Suppress("UNCHECKED_CAST")
class CategoryViewModelFactory(
    private val repository: CategoryRepo
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)){
            return CategoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}