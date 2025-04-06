package com.hvuitsme.admin.data.remote

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.hvuitsme.admin.data.model.Carousel
import kotlinx.coroutines.tasks.await

class BannerDataResource {
    private val database = FirebaseDatabase.getInstance()
    private val carouselRef = database.getReference("Banner")

    suspend fun getCarousel(): List<Carousel> {
        return try {
            val snapshot = carouselRef.get().await()
            snapshot.children.mapNotNull { it.getValue(Carousel::class.java) }
        } catch (e: Exception) {
            Log.e("FirebaseDataSource", "Error fetching carousel data: ${e.message}")
            emptyList()
        }
    }

    suspend fun addBanner(carousel: Carousel) {
        try {
            val ref = carouselRef
            ref.push().setValue(carousel).await()
        } catch (e: Exception) {
            Log.e("FirebaseDataSource", "Error adding banner: ${e.message}")
        }
    }

    suspend fun updateBanner(oldUrl: String, updated: Carousel) {
        try {
            val ref = carouselRef
            val querySnapshot = ref.orderByChild("url").equalTo(oldUrl).get().await()
            for (child in querySnapshot.children) {
                child.ref.setValue(updated).await()
            }
        } catch (e: Exception) {
            Log.e("FirebaseDataSource", "Error updating banner: ${e.message}")
        }
    }

    suspend fun deleteBanner(carousel: Carousel) {
        try {
            val ref = carouselRef
            val querySnapshot = ref.orderByChild("url").equalTo(carousel.url).get().await()
            for (child in querySnapshot.children) {
                child.ref.removeValue().await()
            }
        } catch (e: Exception) {
            Log.e("FirebaseDataSource", "Error deleting banner: ${e.message}")
        }
    }
}