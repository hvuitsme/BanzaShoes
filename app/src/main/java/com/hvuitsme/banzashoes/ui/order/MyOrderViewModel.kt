package com.hvuitsme.banzashoes.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.hvuitsme.banzashoes.data.model.Address
import com.hvuitsme.banzashoes.data.model.Order
import com.hvuitsme.banzashoes.data.repository.OrderRepo
import kotlinx.coroutines.launch

class MyOrderViewModel(
    private val orderRepository: OrderRepo
) : ViewModel() {

    private val _orders = MutableLiveData<List<Order>>(emptyList())
    val orders: LiveData<List<Order>> = _orders

    init {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            orderRepository.observeOrders(currentUser.uid) { list ->
                _orders.postValue(list.sortedByDescending { it.timestamp })
            }
        }
    }

    fun loadOnce() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            val list = orderRepository.getOrders(uid)
            _orders.postValue(list.sortedByDescending { it.timestamp })
        }
    }

    fun cancelOrder(order: Order) = viewModelScope.launch {
        orderRepository.updateStatus(order.id, "Cancelled")
    }

    fun changeAddress(orderId: String, newAddress: Address) {
        viewModelScope.launch {
            orderRepository.updateAddress(orderId, newAddress)
        }
    }

    fun reorder(order: Order) {
    }
}