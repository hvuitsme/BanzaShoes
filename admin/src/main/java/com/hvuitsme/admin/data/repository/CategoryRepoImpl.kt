package com.hvuitsme.admin.data.repository

import com.hvuitsme.admin.data.model.Category
import com.hvuitsme.admin.data.remote.CategoryDataSource

class CategoryRepoImpl(
    private val databaseSource: CategoryDataSource
): CategoryRepo {
    override suspend fun getCategories(): List<Category> {
        return databaseSource.getCategories()
    }

    override suspend fun addCategory(category: Category) {
        databaseSource.addCategory(category)
    }

    override suspend fun updateCategory(oldName: String, updated: Category) {
        databaseSource.updateCategory(oldName, updated)
    }

    override suspend fun deleteCategory(category: Category) {
        databaseSource.deleteCategory(category)
    }
}