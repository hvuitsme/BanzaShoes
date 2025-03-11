package com.hvuitsme.banzashoes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hvuitsme.banzashoes.data.model.Product
import com.hvuitsme.banzashoes.databinding.ProductImageContainerBinding

class ProductAdapter(
    private var productItem: List<Product> = emptyList()
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    fun updateDataProduct(productItem: List<Product>) {
        this.productItem = productItem
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        val binding = ProductImageContainerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productItem[position])
    }

    override fun getItemCount(): Int = productItem.size

    class ProductViewHolder(
        private val binding: ProductImageContainerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(productItem: Product) {
            val imageUrls = productItem.imageUrls[0]
            Glide.with(binding.root.context)
                .load(imageUrls)
                .into(binding.ivProduct)
            binding.tvTitle.text = productItem.title
            binding.tvPrice.text = "$${productItem.price}"
        }
    }
}