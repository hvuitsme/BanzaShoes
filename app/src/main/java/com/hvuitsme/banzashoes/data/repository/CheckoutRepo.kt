package com.hvuitsme.banzashoes.data.repository

import Order

interface CheckoutRepo {
    suspend fun createOrder(order: Order): Boolean
}