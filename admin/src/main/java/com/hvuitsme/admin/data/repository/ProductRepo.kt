package com.hvuitsme.admin.data.repository

import com.hvuitsme.admin.data.model.Product

interface ProductRepo {
    suspend fun getProducts(): List<Product>
    suspend fun addProduct(product: Product)
    suspend fun updateProduct(oldUrl: String, updated: Product)
    suspend fun deleteProduct(product: Product)
}