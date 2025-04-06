package com.hvuitsme.admin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hvuitsme.admin.R
import com.hvuitsme.admin.databinding.ItemProductImageBinding

class ProductImageAdapter(
    private var imageUrls: List<String> = emptyList(),
    private val onAddClick: () -> Unit,
    private val onDelImageClick: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_IMAGE = 0
        private const val VIEW_TYPE_ADD = 1
    }

    inner class ImageViewHolder(private val binding: ItemProductImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(url: String) {
            binding.btnAddImage.visibility = View.GONE
            Glide.with(binding.root.context).load(url).into(binding.ivProductImage)
            binding.btnDeleteImage.setOnClickListener { onDelImageClick(url) }
            binding.btnDeleteImage.visibility = View.VISIBLE
        }
    }

    inner class AddViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {
            itemView.findViewById<View>(R.id.btnAddImage).apply {
                visibility = View.VISIBLE
                setOnClickListener { onAddClick() }
            }
        }
    }

    override fun getItemCount(): Int = imageUrls.size + 1

    override fun getItemViewType(position: Int): Int =
        if (position < imageUrls.size) VIEW_TYPE_IMAGE else VIEW_TYPE_ADD

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemProductImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return if (viewType == VIEW_TYPE_IMAGE) {
            ImageViewHolder(binding)
        } else {
            AddViewHolder(binding.root)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ImageViewHolder && position < imageUrls.size) {
            holder.bind(imageUrls[position])
        } else if (holder is AddViewHolder) {
            holder.bind()
        }
    }

    fun updateData(newUrls: List<String>) {
        imageUrls = newUrls
        notifyDataSetChanged()
    }
}