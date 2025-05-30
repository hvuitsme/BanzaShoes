package com.hvuitsme.banzashoes.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hvuitsme.banzashoes.data.repository.BanzaRepo

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(
    private val repository: BanzaRepo,
    private val state: SavedStateHandle
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T{
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)){
            return HomeViewModel(repository, state) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}