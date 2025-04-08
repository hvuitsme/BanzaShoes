package com.hvuitsme.banzashoes.data.remote

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.hvuitsme.banzashoes.data.model.Carousel
import com.hvuitsme.banzashoes.data.model.Category
import com.hvuitsme.banzashoes.data.model.Product
import kotlinx.coroutines.tasks.await

class FirebaseDataSource {
    private val database = FirebaseDatabase.getInstance()

    suspend fun getCarousel(): List<Carousel> {
        return try {
            val snapshot = database
                .getReference("Banner")
                .get()
                .await()
            snapshot.children.mapNotNull { it.getValue(Carousel::class.java) }
        } catch (e: Exception) {
            Log.e("FirebaseDataSource", "Error fetching carousel data: ${e.message}")
            emptyList()
        }
    }

    suspend fun getCategories(): List<Category> {
        return try {
            val snapshot = database
                .getReference("Categories")
                .get()
                .await()
            snapshot.children.mapNotNull { it.getValue(Category::class.java) }
        } catch (e: Exception) {
            Log.e("FirebaseDataSource", "Error fetching categories data: ${e.message}")
            emptyList()
        }
    }

    suspend fun getProductsByCategory(cateId: String): List<Product> {
        return try {
            val snapshot = database
                .getReference("Product")
                .orderByChild("cateId")
                .equalTo(cateId)
                .get()
                .await()
            snapshot.children.mapNotNull { it.getValue(Product::class.java) }
        } catch (e: Exception) {
            Log.e("FirebaseDataSource", "Error fetching products data: ${e.message}")
            emptyList()
        }
    }

    suspend fun getProductsById(productId: String): Product? {
        return try {
            val snapshot = database
                .getReference("Product")
                .orderByChild("id")
                .equalTo(productId)
                .get()
                .await()
            snapshot.children.firstOrNull()?.getValue(Product::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseDataSource", "Error fetching product by id ${e.message}")
            null
        }
    }

    suspend fun getAllProducts(): List<Product> {
        return try {
            val snapshot = database.getReference("Product").get().await()
            snapshot.children.mapNotNull { it.getValue(Product::class.java) }
        } catch (e: Exception) {
            Log.e("FirebaseDataSource", "Error fetching all products: ${e.message}")
            emptyList()
        }
    }
}