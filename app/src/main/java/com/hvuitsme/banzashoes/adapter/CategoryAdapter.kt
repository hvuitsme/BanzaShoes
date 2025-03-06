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

        private fun convertDriveLink(driveLink: String): String {
            val regex = "/d/([a-zA-Z0-9-_]+)/".toRegex()
            val matchResult = regex.find(driveLink)
            val fileId = matchResult?.groups?.get(1)?.value ?: ""
            return if (fileId.isNotEmpty()) {
                "https://drive.google.com/uc?id=$fileId"
            } else {
                driveLink
            }
        }

        fun setImage(categoryItem: Category) {
            val imageUrl = convertDriveLink(categoryItem.url)
            Glide.with(binding.root.context)
                .load(imageUrl)
                .into(binding.imageCategory)
        }
    }
}
