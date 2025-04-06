package com.hvuitsme.admin.data.repository

import com.hvuitsme.admin.data.model.Category

interface CategoryRepo {
    suspend fun getCategories(): List<Category>
    suspend fun addCategory(category: Category)
    suspend fun updateCategory(oldName: String, updated: Category)
    suspend fun deleteCategory(category: Category)
}