package com.hvuitsme.banzashoes.data.repository

import com.hvuitsme.banzashoes.data.model.Order
import com.hvuitsme.banzashoes.data.remote.CheckoutDataSource

class CheckoutRepoImpl(
    private val checkoutDataSource: CheckoutDataSource
): CheckoutRepo {
    override suspend fun createOrder(order: Order): Boolean {
        return checkoutDataSource.createOrder(order)
    }
}