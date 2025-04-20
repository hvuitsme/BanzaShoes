package com.hvuitsme.admin.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hvuitsme.admin.data.model.Order
import com.hvuitsme.admin.databinding.ItemRecentOrderBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecentOrderAdapter(
    private var orders: List<Order>,
    private val onItemClick: (Order) -> Unit
) : RecyclerView.Adapter<RecentOrderAdapter.OrderViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    inner class OrderViewHolder(private val binding: ItemRecentOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.tvOrderId.text = order.id
            val prefix = if (order.status == "Cancelled") "-" else "+"
            binding.tvTotal.text = "$prefix$${order.total}"
            val color = if (order.status == "Cancelled") Color.RED else Color.BLACK
            binding.tvTotal.setTextColor(color)
            binding.tvTimestamp.text = dateFormat.format(Date(order.timestamp))
            binding.root.setOnClickListener { onItemClick(order) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemRecentOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    fun updateData(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}