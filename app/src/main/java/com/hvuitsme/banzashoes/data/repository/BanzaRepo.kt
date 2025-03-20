package com.hvuitsme.banzashoes.data.repository

import com.hvuitsme.banzashoes.data.model.Carousel
import com.hvuitsme.banzashoes.data.model.Category
import com.hvuitsme.banzashoes.data.model.Product

interface BanzaRepo {
    suspend fun getCarousel(): List<Carousel>
    suspend fun getCategories(): List<Category>
    suspend fun getProductsByCategory(cateId: String): List<Product>
}