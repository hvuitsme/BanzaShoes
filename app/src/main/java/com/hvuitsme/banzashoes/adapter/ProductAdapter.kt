package com.hvuitsme.banzashoes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hvuitsme.banzashoes.R
import com.hvuitsme.banzashoes.data.model.Product
import com.hvuitsme.banzashoes.databinding.ProductContainerBinding

class ProductAdapter(
    private var productItem: List<Product> = emptyList(),
    private val onAddCartClick: (Product) -> Unit,
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val addedProductIds = mutableSetOf<String>()

    fun updateDataProduct(productItem: List<Product>) {
        this.productItem = productItem
        notifyDataSetChanged()
    }

    fun markProductAsAdded(productId: String) {
        addedProductIds.add(productId)
        val index = productItem.indexOfFirst { it.id == productId }
        if (index != -1) {
            notifyItemChanged(index)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        val binding = ProductContainerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductViewHolder(binding, onAddCartClick, onProductClick, addedProductIds)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productItem[position])
    }

    override fun getItemCount(): Int = productItem.size

    class ProductViewHolder(
        private val binding: ProductContainerBinding,
        private val onAddCartClick: (Product) -> Unit,
        private val onProductClick: (Product) -> Unit,
        private val addedProductIds: Set<String>
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(productItem: Product) {
            val imageUrls = productItem.imageUrls.firstOrNull()
            Glide.with(binding.root.context)
                .load(imageUrls)
                .into(binding.ivProduct)
            binding.tvTitle.text = productItem.title
            binding.tvPrice.text = "$${productItem.price}"

            binding.ivAddCart.setImageResource(if (addedProductIds.contains(productItem.id)) R.drawable.ic_cart_check else R.drawable.ic_cart_plus)
            binding.ivAddCart.setOnClickListener {
                onAddCartClick(productItem)
            }
            binding.root.setOnClickListener {
                onProductClick(productItem)
            }
        }
    }
}