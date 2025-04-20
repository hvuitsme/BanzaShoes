package com.hvuitsme.admin.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.hvuitsme.admin.data.model.Order
import com.hvuitsme.admin.data.repository.OrderRepo
import kotlinx.coroutines.launch

class HomeAdminViewModel(
    private val repository: OrderRepo
) : ViewModel() {
    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> = _orders

    init {
        repository.observeOrder { list ->
            val recent = list.sortedByDescending { it.timestamp }.take(5)
            _orders.postValue(recent)
        }
    }


    fun updateStatus(order: Order, newStatus: String) {
        viewModelScope.launch {
            repository.updateStatus(order.id, newStatus)
        }
    }
}
