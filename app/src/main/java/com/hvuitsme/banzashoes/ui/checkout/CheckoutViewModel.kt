package com.hvuitsme.banzashoes.ui.checkout

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.hvuitsme.banzashoes.data.model.Address
import com.hvuitsme.banzashoes.data.model.CartDisplayItem
import com.hvuitsme.banzashoes.data.model.Order
import com.hvuitsme.banzashoes.data.repository.CartRepo
import com.hvuitsme.banzashoes.data.repository.CheckoutRepo
import com.hvuitsme.banzashoes.payment.zalopay.ZaloPayRepository
import com.hvuitsme.banzashoes.payment.zalopay.api.ZaloPayApi
import com.hvuitsme.banzashoes.payment.zalopay.config.ZaloPayConfig
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CheckoutViewModel(
    private val cartRepo: CartRepo,
    private val checkoutRepo: CheckoutRepo
) : ViewModel() {
    val cartDisplayItems = MutableLiveData<List<CartDisplayItem>>()
    val subtotal          = MutableLiveData<Double>()
    val shipping          = MutableLiveData<Double>()
    val total             = MutableLiveData<Double>()
    val loading           = MutableLiveData<Boolean>()
    val paymentToken      = MutableLiveData<String?>()
    val selectedAddress   = MutableLiveData<Address?>()
    internal val _paymentMethod = MutableLiveData<String>()

    companion object{
        private const val USD_TO_VND_RATE = 23_500
    }

    fun setPaymentMethod(method: String) {
        _paymentMethod.value = method
    }

    fun setSelectedAddress(address: Address?) {
        selectedAddress.value = address
    }

    fun loadCartDetails() {
        viewModelScope.launch {
            val items = cartRepo.getCartDisplayItems()
            cartDisplayItems.value = items
            val sub = items.sumOf { it.price * it.quantity }
            subtotal.value = sub
            shipping.value = if (sub > 0) 10.0 else 0.0
            total.value = sub + shipping.value!!
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createZaloPayOrder() {
        val usdTotal = total.value ?: 0.0
        val vndTotal = (usdTotal * USD_TO_VND_RATE).toLong()

        viewModelScope.launch {
            loading.value = true
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl(ZaloPayConfig.ZP_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val api  = retrofit.create(ZaloPayApi::class.java)
                val repo = ZaloPayRepository(api)
                paymentToken.value = repo.createOrder(vndTotal)
            } catch (_: Exception) {
                paymentToken.value = null
            } finally {
                loading.value = false
            }
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
            val addr = selectedAddress.value
                ?: Address("", user.uid, "No information", "", "", false)
            val order = Order(
                userId        = user.uid,
                address       = addr,
                cartItems     = cartDisplayItems.value ?: emptyList(),
                paymentMethod = _paymentMethod.value ?: "COD",
                subtotal      = subtotal.value ?: 0.0,
                shipping      = shipping.value ?: 0.0,
                total         = total.value ?: 0.0,
                status        = "Pending"
            )
            val ok = checkoutRepo.createOrder(order)
            if (ok) {
                val cleared = cartRepo.clearCart()
                loading.value = false
                callback(cleared)
            } else {
                loading.value = false
                callback(false)
            }
        }
    }
}