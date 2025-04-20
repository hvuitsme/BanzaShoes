package com.hvuitsme.admin.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hvuitsme.admin.data.repository.OrderRepo

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(
    private val repository: OrderRepo
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeAdminViewModel::class.java)) {
            return HomeAdminViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}