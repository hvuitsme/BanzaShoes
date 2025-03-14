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

    private var selectedPosition = 0

    fun setSelectedPosition(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }

    fun getSelectedPosition() = selectedPosition

    private var onCategoryClick: ((Category, Int) -> Unit)? = null
    fun setOnCategoryClick(listener: (Category, Int) -> Unit) {
        onCategoryClick = listener
    }

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

    override fun onBindViewHolder( holder: CategoryViewHolder, position: Int) {
        holder.bind(categoryItem[position], position)
    }

    override fun getItemCount(): Int = categoryItem.size

    inner class CategoryViewHolder(
        private val binding: CategoryImageContainerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(categoryItem: Category, position: Int) {
            Glide.with(binding.root.context)
                .load(categoryItem.url)
                .into(binding.imageCategory)

            if (position == selectedPosition) {
                binding.imageCategory.alpha = 1f
            }else{
                binding.imageCategory.alpha = 0.5f
            }

            binding.root.setOnClickListener {
//                setSelectedPosition(position)
                onCategoryClick?.invoke(categoryItem, position)
            }
        }
    }
}