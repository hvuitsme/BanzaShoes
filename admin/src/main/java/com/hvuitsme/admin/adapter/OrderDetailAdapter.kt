package com.hvuitsme.admin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hvuitsme.admin.data.model.OrderItem
import com.hvuitsme.admin.databinding.ItemDetailOrderBinding

class OrderDetailAdapter(
    private var items: List<OrderItem>
) : RecyclerView.Adapter<OrderDetailAdapter.DetailViewHolder>() {

    inner class DetailViewHolder(private val binding: ItemDetailOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OrderItem) {
            binding.tvTitle.text = item.title
            binding.tvPrice.text = "$${item.price}"
            binding.tvSize.text = "Size: ${item.size}"
            binding.tvQty.text = "Qty: ${item.quantity}"
            Glide.with(binding.ivProduct.context)
                .load(item.imageUrls)
                .into(binding.ivProduct)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val binding = ItemDetailOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<OrderItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}