package com.hvuitsme.banzashoes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hvuitsme.banzashoes.databinding.DetailImageContainerBinding

class ImageDetailAdapter(
    private var imageItem: List<String> = emptyList()
) : RecyclerView.Adapter<ImageDetailAdapter.ImageDetailViewHolder>() {

    fun updateDataImage(imageItem: List<String>) {
        this.imageItem = imageItem
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageDetailViewHolder {
        val binding = DetailImageContainerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ImageDetailViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ImageDetailViewHolder,
        position: Int
    ) {
        holder.bind(imageItem[position])
    }

    override fun getItemCount(): Int = imageItem.size

    inner class ImageDetailViewHolder(
        private val binding: DetailImageContainerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: String) {
            Glide
                .with(binding.root.context)
                .load(image)
                .into(binding.ivDetail)
        }
    }
}