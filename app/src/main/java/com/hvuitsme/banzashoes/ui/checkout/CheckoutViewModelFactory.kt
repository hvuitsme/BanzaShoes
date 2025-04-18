package com.hvuitsme.banzashoes.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hvuitsme.banzashoes.data.repository.CartRepo
import com.hvuitsme.banzashoes.data.repository.CheckoutRepo
import com.hvuitsme.banzashoes.payment.zalopay.ZaloPayRepository

@Suppress("UNCHECKED_CAST")
class CheckoutViewModelFactory(
    private val cartRepo: CartRepo,
    private val checkoutRepo: CheckoutRepo
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CheckoutViewModel::class.java)) {
            return CheckoutViewModel(cartRepo, checkoutRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}