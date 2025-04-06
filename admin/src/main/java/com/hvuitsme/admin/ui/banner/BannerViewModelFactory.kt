package com.hvuitsme.admin.ui.banner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hvuitsme.admin.data.repository.BannerRepo

@Suppress("UNCHECKED_CAST")
class BannerViewModelFactory(
    private val repository: BannerRepo
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BannerViewModel::class.java)) {
            return BannerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}