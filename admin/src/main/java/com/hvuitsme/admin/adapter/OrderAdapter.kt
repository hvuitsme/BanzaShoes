package com.hvuitsme.admin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hvuitsme.admin.data.model.Order
import com.hvuitsme.admin.databinding.AdminOrderContainerBinding

class OrderAdapter(
    private var orderList: List<Order>,
    private val onItemClick: (Order) -> Unit,
    private val onStatusChange: ((Order, String) -> Unit)? = null
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderViewHolder {
        val binding = AdminOrderContainerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int = orderList.size

    fun updateData(newOrders: List<Order>) {
        orderList = newOrders
        notifyDataSetChanged()
    }

    inner class OrderViewHolder(
        private val binding: AdminOrderContainerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.tvOrderId.text = order.id
            binding.tvStatus.text = order.status
            binding.tvTotal.text = order.total.toString()

            binding.root.setOnClickListener {
                onItemClick(order)
            }
        }
    }
}
