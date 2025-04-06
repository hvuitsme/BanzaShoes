package com.hvuitsme.admin.ui.category

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hvuitsme.admin.data.model.Category
import com.hvuitsme.admin.data.repository.CategoryRepo
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val repository: CategoryRepo
) : ViewModel() {
    val categories = MutableLiveData<List<Category>>()

    fun loadCategories() {
        viewModelScope.launch {
            val data = repository.getCategories()
            categories.postValue(data)
        }
    }

    fun addCategory(brand: String, url: String, onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            try {
                repository.addCategory(Category(cateId = brand, url = url))
                loadCategories()
                onSuccess()
            }catch (e: Exception){
                onError(e)
            }
        }
    }

    fun updateCategory(
        oldUrl: String,
        brand: String,
        newUrl: String,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.updateCategory(oldUrl, Category(cateId = brand, url = newUrl))
                loadCategories()
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun deleteCategory(
        category: Category,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.deleteCategory(category)
                loadCategories()
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}