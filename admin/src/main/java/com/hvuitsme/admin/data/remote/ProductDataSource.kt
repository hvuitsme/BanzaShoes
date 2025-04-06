package com.hvuitsme.admin.data.remote

import com.google.firebase.database.FirebaseDatabase
import com.hvuitsme.admin.data.model.Product
import kotlinx.coroutines.tasks.await

class ProductDataSource {
    private val database = FirebaseDatabase.getInstance()
    private val productRef = database.getReference("Product")

    suspend fun getProducts(): List<Product> {
        val snapshot = productRef.get().await()
        return snapshot.children.mapNotNull { it.getValue(Product::class.java) }
    }

    suspend fun addProduct(product: Product) {
        if (product.id.isEmpty() || product.id == "0") {
            val currentProducts = getProducts()
            val maxNumber = currentProducts.mapNotNull {
                it.id.takeIf { id -> id.startsWith("shoes_") }?.removePrefix("shoes_")?.toIntOrNull()
            }.maxOrNull() ?: 0
            val newNumber = maxNumber + 1
            val newId = "shoes_" + newNumber.toString().padStart(3, '0')
            val newProduct = product.copy(id = newId)
            productRef.child(newId).setValue(newProduct).await()
        }else{
            productRef.child(product.id).setValue(product).await()
        }
    }

    suspend fun updateProduct(oldUrl: String, product: Product) {
        productRef.child(product.id).setValue(product).await()
    }

    suspend fun deleteProduct(product: Product) {
        productRef.child(product.id).removeValue().await()
    }
}