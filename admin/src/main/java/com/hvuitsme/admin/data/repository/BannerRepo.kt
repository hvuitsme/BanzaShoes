package com.hvuitsme.admin.data.repository

import com.hvuitsme.admin.data.model.Carousel

interface BannerRepo {
    suspend fun getCarousel(): List<Carousel>
    suspend fun addBanner(carousel: Carousel)
    suspend fun updateBanner(oldUrl: String, updated: Carousel)
    suspend fun deleteBanner(carousel: Carousel)
}