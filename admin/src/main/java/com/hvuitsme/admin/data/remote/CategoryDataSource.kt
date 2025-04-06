package com.hvuitsme.admin.data.remote

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.hvuitsme.admin.data.model.Category
import kotlinx.coroutines.tasks.await

class CategoryDataSource {
    private val database = FirebaseDatabase.getInstance()
    private val categoryRef = database.getReference("Categories")

    suspend fun getCategories(): List<Category> {
        val snapshot = categoryRef.get().await()
        return snapshot.children.mapNotNull { it.getValue(Category::class.java) }
    }

    suspend fun addCategory(category: Category) {
        try {
            val ref = categoryRef
            ref.push().setValue(category).await()
        } catch (e: Exception) {
            Log.e("FirebaseDataSource", "Error adding category: ${e.message}")
        }
    }

    suspend fun updateCategory(oldName: String, updated: Category) {
        try {
            val ref = categoryRef
            val querySnapshot = ref.orderByChild("url").equalTo(oldName).get().await()
            for (child in querySnapshot.children) {
                child.ref.setValue(updated).await()
            }
        } catch (e: Exception) {
            Log.e("FirebaseDataSource", "Error updating category: ${e.message}")
        }
    }

    suspend fun deleteCategory(category: Category) {
        try {
            val querySnapshot = categoryRef.orderByChild("cateId").equalTo(category.cateId).get().await()
            for (child in querySnapshot.children) {
                child.ref.removeValue().await()
            }
        } catch (e: Exception) {
            Log.e("FirebaseDataSource", "Error deleting category: ${e.message}")
        }
    }
}