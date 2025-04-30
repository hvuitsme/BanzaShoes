package com.hvuitsme.banzashoes.ui.address

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hvuitsme.banzashoes.data.model.Address
import com.hvuitsme.banzashoes.data.repository.AddressRepo
import kotlinx.coroutines.launch

class AddressViewModel(
    private val repository: AddressRepo
) : ViewModel() {
    private val _addressList = MutableLiveData<List<Address>>()
    val addressList: LiveData<List<Address>> get() = _addressList

    private val _selectedAddress = MutableLiveData<Address?>()
    val selectedAddress: LiveData<Address?> get() = _selectedAddress

    fun setSelectedAddress(address: Address?) {
        _selectedAddress.value = address
    }

    fun loadAddress(userId: String) {
        viewModelScope.launch {
            val addresses = repository.getAddress(userId)
            _addressList.value = addresses
        }
    }

    fun addAddress(address: Address, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            if (address.dfAddress) {
                val orther = repository.getAddress(address.userId)
                    .filter { it.dfAddress }
                orther.forEach { old ->
                    val updated = old.copy(dfAddress = false)
                    repository.updateAddress(updated)
                }
            }
            val success = repository.addAddress(address)
            if (success) _addressList.value = repository.getAddress(address.userId)
            onResult(success)
        }
    }

    fun updateAddress(address: Address, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            if (address.dfAddress) {
                val orther = repository.getAddress(address.userId)
                    .filter { it.dfAddress && it.id != address.id }
                orther.forEach { old ->
                    val updated = old.copy(dfAddress = false)
                    repository.updateAddress(updated)
                }
                val success = repository.updateAddress(address)
                if (success) {
                    _addressList.value = repository.getAddress(address.userId)
                    _selectedAddress.value = null
                }
                onResult(success)
            }
        }
    }

    fun deleteAddress(address: Address, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.deleteAddress(address)
            if (success) _addressList.value = repository.getAddress(address.userId)
            onResult(success)
        }
    }
}