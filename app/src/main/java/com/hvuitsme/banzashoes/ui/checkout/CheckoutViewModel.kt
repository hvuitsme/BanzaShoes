package com.hvuitsme.banzashoes.ui.checkout

import Order
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.hvuitsme.banzashoes.data.model.Address
import com.hvuitsme.banzashoes.data.model.CartDisplayItem
import com.hvuitsme.banzashoes.data.repository.CartRepo
import com.hvuitsme.banzashoes.data.repository.CheckoutRepo
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val cartRepo: CartRepo,
    private val checkoutRepo: CheckoutRepo
) : ViewModel() {
    val cartDisplayItems = MutableLiveData<List<CartDisplayItem>>()
    val subtotal = MutableLiveData<Double>()
    val shipping = MutableLiveData<Double>()
    val total = MutableLiveData<Double>()
    val loading = MutableLiveData<Boolean>()

    private val _paymentMethod = MutableLiveData<String>()
    fun setPaymentMethod(method: String) {
        _paymentMethod.value = method
    }

    val selectedAddress = MutableLiveData<Address?>()
    fun setSelectedAddress(address: Address?) {
        selectedAddress.value = address
    }

    fun loadCartDetails() {
        viewModelScope.launch {
            val items = cartRepo.getCartDisplayItems()
            cartDisplayItems.value = items
            val sub = items.sumOf { it.price * it.quantity }
            subtotal.value = sub
            val shipFee = if (sub > 0) 10.0 else 0.0
            shipping.value = shipFee
            total.value = sub + shipFee
        }
    }

    fun processPayment(callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            loading.value = true
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                loading.value = false
                callback(false)
                return@launch
            }

            val address = selectedAddress.value ?: Address(
                id = "",
                userId = user.uid,
                name = "Not infomation",
                phone = "",
                address = "",
                dfAddress = false
            )

            val order = Order(
                userId = user.uid,
                address = address,
                cartItems = cartDisplayItems.value ?: emptyList(),
                paymentMethod = _paymentMethod.value ?: "COD",
                subtotal = subtotal.value ?: 0.0,
                shipping = shipping.value ?: 0.0,
                total = total.value ?: 0.0
            )

            val orderSuccess = checkoutRepo.createOrder(order)
            if (orderSuccess) {
                val clearSuccess = cartRepo.clearCart()
                loading.value = false
                callback(clearSuccess)
            } else {
                loading.value = false
                callback(false)
            }
        }
    }
}