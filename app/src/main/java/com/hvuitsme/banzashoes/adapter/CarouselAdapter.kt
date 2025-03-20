package com.hvuitsme.banzashoes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hvuitsme.banzashoes.data.model.Carousel
import com.hvuitsme.banzashoes.databinding.CarouselContailnerBinding

class CarouselAdapter(
    private var carouselItems: List<Carousel>
) : RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    fun updateDataCarousel(carouselItems: List<Carousel>) {
        this.carouselItems = carouselItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val binding = CarouselContailnerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CarouselViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        if (carouselItems.isNotEmpty()) {
            val actualPosition = position % carouselItems.size
            holder.setImage(carouselItems[actualPosition])
        }
    }

    override fun getItemCount(): Int = if (carouselItems.isEmpty()) 0 else Int.MAX_VALUE

    class CarouselViewHolder(
        private val binding: CarouselContailnerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun setImage(carouselItem: Carousel) {
            Glide.with(binding.root.context)
                .load(carouselItem.url)
                .into(binding.imageCarousel)
        }
    }
}