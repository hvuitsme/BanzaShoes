package com.hvuitsme.banzashoes.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.hvuitsme.banzashoes.data.model.Carousel
import com.hvuitsme.banzashoes.databinding.CarouselImageContailnerBinding

class CarouselAdapter(
    private var carouselItems: List<Carousel>
) : RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    fun updateData(newItems: List<Carousel>) {
        carouselItems = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val binding = CarouselImageContailnerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CarouselViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        // Sử dụng modulo để tính vị trí thực sự của item
        if (carouselItems.isNotEmpty()) {
            val actualPosition = position % carouselItems.size
            holder.setImage(carouselItems[actualPosition])
        }
    }

    override fun getItemCount(): Int = if (carouselItems.isEmpty()) 0 else Int.MAX_VALUE

    class CarouselViewHolder(
        private val binding: CarouselImageContailnerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("CheckResult")
        fun setImage(carouselItem: Carousel) {
            val radius = 20
            Glide.with(binding.root.context)
                .load(carouselItem.url)
                .into(binding.imageCarousel)
        }
    }
}