package com.hvuitsme.admin.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.hvuitsme.admin.data.model.Order
import com.hvuitsme.admin.data.repository.OrderRepo
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeAdminViewModel(
    private val repository: OrderRepo
) : ViewModel() {
    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> = _orders

    private val _revenueByTime = MutableLiveData<Map<String, Double>>()
    val revenueByTime: LiveData<Map<String, Double>> = _revenueByTime

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

    fun loadRevenue(timeframe: Timeframe) {
        repository.observeOrder { list ->
            val pattern = when (timeframe) {
                Timeframe.DAY -> "MM-dd"
                Timeframe.MONTH -> "yyyy-MM"
                Timeframe.YEAR -> "yyyy"
            }
            val sdf = SimpleDateFormat(pattern, Locale.getDefault())

            val success = list.filter { it.status == "Success" }
            val groupedSuccess = success.groupBy { sdf.format(Date(it.timestamp)) }
            val summedSuccess = groupedSuccess.mapValues { it.value.sumOf { it.total } }
                .toSortedMap()
            _revenueByTime.postValue(summedSuccess)
        }
    }
}
