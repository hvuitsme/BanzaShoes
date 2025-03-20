package com.hvuitsme.banzashoes.data.remote.rest

import com.hvuitsme.banzashoes.data.model.Carousel
import com.hvuitsme.banzashoes.data.model.Category
import com.hvuitsme.banzashoes.data.model.Product
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("banner")
    suspend fun getCarousel(): Response<List<Carousel>>

    @GET("categories")
    suspend fun getCategories(): Response<List<Category>>

    @GET("products")
    suspend fun getProductsByCateId(
        @Query("cateId") cateId: String
    ): Response<List<Product>>
}