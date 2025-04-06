package com.hvuitsme.admin.data.repository

import com.hvuitsme.admin.data.model.Carousel
import com.hvuitsme.admin.data.remote.BannerDataResource

class BannerRepoImpl(
    private val databaseSource: BannerDataResource
): BannerRepo {
    override suspend fun getCarousel(): List<Carousel> {
        return databaseSource.getCarousel()
    }

    override suspend fun addBanner(carousel: Carousel) {
        databaseSource.addBanner(carousel)
    }

    override suspend fun updateBanner(oldUrl: String, updated: Carousel) {
        databaseSource.updateBanner(oldUrl, updated)
    }

    override suspend fun deleteBanner(carousel: Carousel) {
        databaseSource.deleteBanner(carousel)
    }
}