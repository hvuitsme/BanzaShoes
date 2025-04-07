package com.hvuitsme.banzashoes.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hvuitsme.banzashoes.data.repository.AuthRepo

@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory(
    private val repository: AuthRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}