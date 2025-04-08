package com.hvuitsme.banzashoes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hvuitsme.banzashoes.R
import com.hvuitsme.banzashoes.data.model.Product

class SearchProductAdapter(
    private var products: List<Product> = emptyList(),
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<SearchProductAdapter.SearchProductViewHolder>() {

    fun updateDataSearch(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search, parent, false)
        return SearchProductViewHolder(view, onProductClick)
    }

    override fun onBindViewHolder(holder: SearchProductViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = products.size

    class SearchProductViewHolder(itemView: View, private val onProductClick: (Product) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val ivSearchProduct: ImageView = itemView.findViewById(R.id.ivSearchProduct)
        private val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)

        fun bind(product: Product) {
            tvProductName.text = product.title
            tvProductPrice.text = "$${product.price}"
            if (product.imageUrls.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(product.imageUrls.firstOrNull())
                    .into(ivSearchProduct)
            }
            itemView.setOnClickListener {
                onProductClick(product)
            }
        }
    }
}