package com.hvuitsme.admin.data.repository

import com.hvuitsme.admin.data.model.Product
import com.hvuitsme.admin.data.remote.ProductDataSource

class ProductRepoImpl(
    private val databaseSource: ProductDataSource
): ProductRepo {
    override suspend fun getProducts(): List<Product> {
        return databaseSource.getProducts()
    }

    override suspend fun addProduct(product: Product) {
        databaseSource.addProduct(product)
    }

    override suspend fun updateProduct(
        oldUrl: String,
        updated: Product
    ) {
        databaseSource.updateProduct(oldUrl, updated)
    }

    override suspend fun deleteProduct(product: Product) {
        databaseSource.deleteProduct(product)
    }
}