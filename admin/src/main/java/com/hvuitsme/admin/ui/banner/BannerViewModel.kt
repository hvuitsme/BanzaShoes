package com.hvuitsme.admin.ui.banner

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hvuitsme.admin.data.model.Carousel
import com.hvuitsme.admin.data.repository.BannerRepo
import kotlinx.coroutines.launch

class BannerViewModel(
    private val repository: BannerRepo
) : ViewModel() {
    val carousel = MutableLiveData<List<Carousel>>()

    fun loadCarousel() {
        viewModelScope.launch {
            val data = repository.getCarousel()
            carousel.value = data
        }
    }

    fun addBanner(url: String, onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            try {
                repository.addBanner(Carousel(url = url))
                loadCarousel()
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun updateBanner(oldUrl: String, newUrl: String, onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            try {
                repository.updateBanner(oldUrl, Carousel(url = newUrl))
                loadCarousel()
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun deleteBanner(banner: Carousel, onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            try {
                repository.deleteBanner(banner)
                loadCarousel()
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}