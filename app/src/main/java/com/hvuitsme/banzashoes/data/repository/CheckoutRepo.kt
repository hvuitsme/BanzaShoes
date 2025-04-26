package com.hvuitsme.banzashoes.data.repository

import com.hvuitsme.banzashoes.data.model.Order

interface CheckoutRepo {
    suspend fun createOrder(order: Order): Boolean
}