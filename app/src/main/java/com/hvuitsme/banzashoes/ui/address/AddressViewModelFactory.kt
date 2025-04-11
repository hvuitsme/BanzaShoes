package com.hvuitsme.banzashoes.ui.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hvuitsme.banzashoes.data.repository.AddressRepo

@Suppress("UNCHECKED_CAST")
class AddressViewModelFactory(
    private val repository: AddressRepo
): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddressViewModel::class.java)) {
            return AddressViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}