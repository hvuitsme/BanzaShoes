package com.hvuitsme.banzashoes.ui.order

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hvuitsme.banzashoes.data.model.Order
import com.hvuitsme.banzashoes.databinding.ItemMyOrderBinding
import java.text.NumberFormat
import java.util.*

class OrderAdapter(
    private val onItemClicked: (Order) -> Unit,
    private val onAction: (Order, Action) -> Unit
) : ListAdapter<Order, OrderAdapter.VH>(DiffCallback()) {

    enum class Action { CANCEL, REVIEW, REORDER }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemMyOrderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val b: ItemMyOrderBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(o: Order) {
            b.tvOrderId.text = "Order ID: ${o.id}"
//            b.tvTotal.text = NumberFormat.getCurrencyInstance(Locale.getDefault()).format(o.total)
            b.tvTotal.text = "$${o.total}"
            b.rvProducts.adapter = ProductThumbAdapter(o.cartItems)
            b.root.setOnClickListener { onItemClicked(o) }
            when (o.status) {
                "Pending", "Processing" -> {
                    b.btnAction1.visibility = View.VISIBLE
                    b.btnAction1.text = "Cancel"
                    b.btnAction1.setOnClickListener { onAction(o, Action.CANCEL) }
                    b.btnAction2.visibility = View.GONE
                }
                "Success" -> {
                    b.btnAction1.visibility = View.VISIBLE
                    b.btnAction1.text = "Reorder"
                    b.btnAction1.setOnClickListener { onAction(o, Action.REORDER) }
                    if (!o.reviewed){
                        b.btnAction2.visibility = View.VISIBLE
                        b.btnAction2.text = "Review"
                        b.btnAction2.setOnClickListener { onAction(o, Action.REVIEW) }
                    }else{
                        b.btnAction2.visibility = View.GONE
                    }
                }
                "Cancelled" -> {
                    b.btnAction1.visibility = View.VISIBLE
                    b.btnAction1.text = "Reorder"
                    b.btnAction1.setOnClickListener { onAction(o, Action.REORDER) }
                    b.btnAction2.visibility = View.GONE
                }
                else -> {
                    b.btnAction1.visibility = View.GONE
                    b.btnAction2.visibility = View.GONE
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(a: Order, b: Order) = a.id == b.id
        override fun areContentsTheSame(a: Order, b: Order) = a == b
    }
}