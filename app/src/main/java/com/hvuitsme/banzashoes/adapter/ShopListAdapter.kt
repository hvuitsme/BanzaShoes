package com.hvuitsme.banzashoes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hvuitsme.banzashoes.data.model.CartDisplayItem
import com.hvuitsme.banzashoes.databinding.ItemCheckoutContainerBinding

class ShopListAdapter(
    private var items: List<CartDisplayItem>
) : RecyclerView.Adapter<ShopListAdapter.ShopListViewHolder>() {

    fun updateItems(newItems: List<CartDisplayItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopListViewHolder {
        val binding = ItemCheckoutContainerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShopListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShopListViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ShopListViewHolder(private val binding: ItemCheckoutContainerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CartDisplayItem) {
            binding.tvTitle.text = item.title
            binding.tvPrice.text = "$${"%.2f".format(item.price)}"
            binding.tvSize.text = "Size: ${item.size}"
            binding.tvQty.text = "x${item.quantity}"
            Glide.with(binding.ivProduct.context)
                .load(item.imageUrls)
                .into(binding.ivProduct)
        }
    }
}
