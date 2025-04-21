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

    private val _orderCountByTime = MutableLiveData<Map<String, Int>>()
    val orderCountByTime: LiveData<Map<String, Int>> = _orderCountByTime

    private val _avgOrderValueByTime = MutableLiveData<Map<String, Double>>()
    val avgOrderValueByTime: LiveData<Map<String, Double>> = _avgOrderValueByTime

    init {
        repository.observeOrder { list ->
            val recent = list.sortedByDescending { it.timestamp }.take(5)
            _orders.postValue(recent)
        }
    }

    fun loadRevenue(timeframe: Timeframe) {
        repository.observeOrder { list ->
            val success = list.filter { it.status == "Success" }
            val pattern = when (timeframe) {
                Timeframe.DAY -> "MM-dd"
                Timeframe.MONTH -> "yyyy-MM"
                Timeframe.YEAR -> "yyyy"
            }
            val sdf = SimpleDateFormat(pattern, Locale.getDefault())

            val grouped = success.groupBy { sdf.format(Date(it.timestamp)) }

            val revMap = grouped.mapValues { entry ->
                entry.value.sumOf { it.total }
            }.toSortedMap()

            val cntMap = grouped.mapValues { entry ->
                entry.value.size
            }.toSortedMap()

            val aovMap = revMap.mapValues { (entry, rev) ->
                val cnt = cntMap[entry] ?: 1
                rev / cnt
            }

            _revenueByTime.postValue(revMap)
            _orderCountByTime.postValue(cntMap)
            _avgOrderValueByTime.postValue(aovMap)
        }
    }
}
