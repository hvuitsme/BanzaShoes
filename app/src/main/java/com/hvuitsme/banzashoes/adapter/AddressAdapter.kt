package com.hvuitsme.banzashoes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.hvuitsme.banzashoes.R
import com.hvuitsme.banzashoes.data.model.Address
import com.hvuitsme.banzashoes.databinding.ItemAddressContainerBinding

class AddressAdapter(
    private var addressItems: List<Address> = emptyList(),
    private var onSelectClick: (Address) -> Unit,
    private val onAddClick: () -> Unit,
    private val onEditClick: (Address) -> Unit,
    private val onDeleteClick: (Address) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun updateDataAddress(newItems: List<Address>) {
        addressItems = newItems
        notifyDataSetChanged()
    }

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_ADD = 1
    }

    abstract class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class ItemViewHolder(
        private val binding: ItemAddressContainerBinding
    ) : AddressViewHolder(binding.root) {
        fun bind(
            item: Address,
            onSelectClick: (Address) -> Unit,
            onEditClick: (Address) -> Unit,
            onDeleteClick: (Address) -> Unit
        ) {
            binding.tvName.text = item.name
            binding.tvPhone.text = item.phone
            binding.tvAddress.text = item.address
            binding.root.setOnClickListener { onSelectClick(item) }
            binding.tvEdit.setOnClickListener { onEditClick(item) }
            // binding.tvDelete?.setOnClickListener { onDeleteClick(item) }
            binding.isDefault.visibility = if (item.dfAddress) View.VISIBLE else View.GONE
        }
    }

    class AddButtonViewHolder(
        itemView: View,
        private val onAddClick: () -> Unit
    ) : AddressViewHolder(itemView) {
        fun bind() {
            itemView.findViewById<MaterialButton>(R.id.addBtn)?.setOnClickListener {
                onAddClick()
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        if (position < addressItems.size) VIEW_TYPE_ITEM else VIEW_TYPE_ADD

    override fun getItemCount(): Int = addressItems.size + 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val binding = ItemAddressContainerBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            ItemViewHolder(binding)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_add_btn, parent, false)
            AddButtonViewHolder(view, onAddClick)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder && position < addressItems.size) {
            holder.bind(addressItems[position], onSelectClick, onEditClick, onDeleteClick)
        } else if (holder is AddButtonViewHolder) {
            holder.bind()
        }
    }
}