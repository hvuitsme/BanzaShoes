package com.hvuitsme.admin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hvuitsme.admin.R
import com.hvuitsme.admin.data.model.Category
import com.hvuitsme.admin.databinding.AdminCategoryContainerBinding

class CategoryAdapter(
    private var categoryItem: List<Category>,
    private val onAddClick: () -> Unit,
    private val onEditClick: (Category) -> Unit,
    private val onDeleteClick: (Category) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_ADD  = 1
    }

    inner class ItemViewHolder(private val binding: AdminCategoryContainerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Category) {
            binding.btnAddImage.visibility = View.GONE
            Glide.with(binding.root.context)
                .load(item.url)
                .into(binding.imageCategory)
            binding.root.setOnClickListener { onEditClick(item) }
            binding.btnDeleteImage.setOnClickListener { onDeleteClick(item) }
            binding.btnDeleteImage.visibility = View.VISIBLE
        }
    }

    inner class AddViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {
            itemView.findViewById<View>(R.id.btnAddImage)?.apply {
                visibility = View.VISIBLE
                setOnClickListener { onAddClick() }
            }

        }
    }

    override fun getItemViewType(position: Int): Int =
        if (position < categoryItem.size) VIEW_TYPE_ITEM else VIEW_TYPE_ADD

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = AdminCategoryContainerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return if (viewType == VIEW_TYPE_ITEM) {
            ItemViewHolder(binding)
        } else {
            AddViewHolder(binding.root)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder && position < categoryItem.size) {
            holder.bind(categoryItem[position])
        } else if (holder is AddViewHolder) {
            holder.bind()
        }
    }

    override fun getItemCount(): Int = categoryItem.size + 1

    fun updateDataCategory(newItems: List<Category>) {
        categoryItem = newItems
        notifyDataSetChanged()
    }
}