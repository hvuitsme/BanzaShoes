package com.hvuitsme.banzashoes.data.repository

import com.hvuitsme.banzashoes.data.model.Carousel
import com.hvuitsme.banzashoes.data.model.Category
import com.hvuitsme.banzashoes.data.model.Product
import com.hvuitsme.banzashoes.data.remote.FirebaseDataSource

class BanzaRepoImpl(
    private val databaseSource: FirebaseDataSource
): BanzaRepo {
    override suspend fun getCarousel(): List<Carousel> {
        return databaseSource.getCarousel()
    }

    override suspend fun getCategories(): List<Category> {
        return databaseSource.getCategories()
    }

    override suspend fun getProductsByCategory(cateId: String): List<Product> {
        return databaseSource.getProductsByCategory(cateId)
    }
}