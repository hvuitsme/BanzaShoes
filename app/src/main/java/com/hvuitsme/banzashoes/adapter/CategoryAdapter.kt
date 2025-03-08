package com.hvuitsme.banzashoes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hvuitsme.banzashoes.data.model.Category
import com.hvuitsme.banzashoes.databinding.CategoryImageContainerBinding

class CategoryAdapter(
    private var categoryItem: List<Category>
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    fun updateDataCategory(categoryItem: List<Category>) {
        this.categoryItem = categoryItem
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = CategoryImageContainerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.setImage(categoryItem[position])
    }

    override fun getItemCount(): Int = categoryItem.size

    class CategoryViewHolder(
        private val binding: CategoryImageContainerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun setImage(categoryItem: Category) {
            Glide.with(binding.root.context)
                .load(categoryItem.url)
                .into(binding.imageCategory)
        }
    }
}