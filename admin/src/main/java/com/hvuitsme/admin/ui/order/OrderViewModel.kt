package com.hvuitsme.admin.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hvuitsme.admin.data.model.Order
import com.hvuitsme.admin.data.repository.OrderRepo
import kotlinx.coroutines.launch

class OrderViewModel(
    private val orderRepository: OrderRepo
) : ViewModel() {
    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> get() = _orders

    init {
        fetchOrders()
    }

    private fun fetchOrders() {
        viewModelScope.launch {
            val list = orderRepository.getOrders()
            _orders.postValue(list)
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            orderRepository.updateStatus(orderId, newStatus)
            fetchOrders()
        }
    }
}