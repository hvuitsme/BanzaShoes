package com.hvuitsme.admin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.hvuitsme.admin.R
import com.hvuitsme.admin.data.model.Carousel
import com.hvuitsme.admin.databinding.AdminCarouselContainerBinding

class CarouselAdapter(
    private var carouselItems: List<Carousel>,
    private val onAddClick: () -> Unit,
    private val onEditClick: (Carousel) -> Unit,
    private val onDeleteClick: (Carousel) -> Unit
) : RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_ADD  = 1
    }

    abstract class CarouselViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class ItemViewHolder(
        private val binding: AdminCarouselContainerBinding
    ) : CarouselViewHolder(binding.root) {

        fun bind(item: Carousel, onEditClick: (Carousel) -> Unit, onDeleteClick: (Carousel) -> Unit) {
            Glide.with(binding.root.context)
                .load(item.url)
                .into(binding.imageCarousel)

            binding.root.setOnClickListener {
                onEditClick(item)
            }
            binding.root.findViewById<View>(R.id.iconDelete)?.setOnClickListener {
                onDeleteClick(item)
            }
        }
    }

    class AddButtonViewHolder(
        itemView: View,
        private val onAddClick: () -> Unit
    ) : CarouselViewHolder(itemView) {

        fun bind() {
            itemView.findViewById<MaterialButton>(R.id.addBtn)?.setOnClickListener {
                onAddClick()
            }
        }
    }

    fun updateDataCarousel(newItems: List<Carousel>) {
        carouselItems = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int =
        if (carouselItems.isNotEmpty()) carouselItems.size + 1 else 0

    override fun getItemViewType(position: Int): Int =
        if (position < carouselItems.size) VIEW_TYPE_ITEM else VIEW_TYPE_ADD

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val binding = AdminCarouselContainerBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
            ItemViewHolder(binding)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_add_btn, parent, false)
            AddButtonViewHolder(view, onAddClick)
        }
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        if (holder is ItemViewHolder && position < carouselItems.size) {
            holder.bind(carouselItems[position], onEditClick, onDeleteClick)
        } else if (holder is AddButtonViewHolder) {
            holder.bind()
        }
    }
}