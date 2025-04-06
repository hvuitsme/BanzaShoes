package com.hvuitsme.admin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.hvuitsme.admin.R
import com.hvuitsme.admin.data.model.Product
import com.hvuitsme.admin.databinding.AdminProductContainerBinding

class ProductAdapter(
    private var productItems: List<Product> = emptyList(),
    private val onAddClick: () -> Unit,
    private val onEditClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_ADD = 1
    }

    class ProductViewHolder(
        private val binding: AdminProductContainerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product, onEditClick: (Product) -> Unit, onDeleteClick: (Product) -> Unit) {
            Glide.with(binding.root.context)
                .load(product.imageUrls.firstOrNull())
                .into(binding.ivProduct)
            binding.tvTitle.text = product.title
            binding.tvPrice.text = product.price.toString()
            binding.root.setOnClickListener { onEditClick(product) }
            binding.ivDelete.setOnClickListener { onDeleteClick(product) }
        }
    }

    class AddButtonViewHolder(
        itemView: View,
        private val onAddClick: () -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        fun bind() {
            itemView.findViewById<MaterialButton>(R.id.addBtn)?.setOnClickListener {
                onAddClick()
            }
        }
    }

    override fun getItemCount(): Int = productItems.size + 1

    override fun getItemViewType(position: Int): Int =
        if (position < productItems.size) VIEW_TYPE_ITEM else VIEW_TYPE_ADD

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val binding = AdminProductContainerBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            ProductViewHolder(binding)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_add_btn, parent, false)
            AddButtonViewHolder(view, onAddClick)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ProductViewHolder && position < productItems.size) {
            holder.bind(productItems[position], onEditClick, onDeleteClick)
        } else if (holder is AddButtonViewHolder) {
            holder.bind()
        }
    }

    fun updateDataProduct(newItems: List<Product>) {
        productItems = newItems
        notifyDataSetChanged()
    }
}