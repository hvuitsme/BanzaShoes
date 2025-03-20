package com.hvuitsme.banzashoes.data.remote.rest

import android.util.Log
import com.hvuitsme.banzashoes.data.model.Carousel
import com.hvuitsme.banzashoes.data.model.Category
import com.hvuitsme.banzashoes.data.model.Product

class RemoteDataSource(
    private val apiService: ApiService
) {
    suspend fun getCarousel(): List<Carousel> {
        return try {
            val response = apiService.getCarousel()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("RemoteDataSource", "Error fetching carousel data: ${e.message}")
            emptyList()
        }
    }

    suspend fun getCategories(): List<Category> {
        return try {
            val response = apiService.getCategories()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("RemoteDataSource", "Error fetching categories data: ${e.message}")
            emptyList()
        }
    }

    suspend fun getProductsByCategory(cateId: String): List<Product> {
        return try {
            val response = apiService.getProductsByCateId(cateId)
            if (response.isSuccessful){
                response.body() ?: emptyList()
            }else{
                emptyList()
            }
        }catch (e: Exception){
            Log.e("RemoteDataSource","Error fetching products data: ${e.message}")
            emptyList()
        }
    }
}