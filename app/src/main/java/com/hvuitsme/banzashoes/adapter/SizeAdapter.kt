package com.hvuitsme.banzashoes.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hvuitsme.banzashoes.data.model.Size
import com.hvuitsme.banzashoes.databinding.SizeContainerBinding

class SizeAdapter(
    private var sizeItem: List<Size> = emptyList(),
    private val onSizeSelected: (Size) -> Unit
) : RecyclerView.Adapter<SizeAdapter.SizeViewHolder>() {

    var selectedPosition = -1

    fun updateDataSize(sizeItem: List<Size>) {
        this.sizeItem = sizeItem
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SizeViewHolder {
        val binding = SizeContainerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SizeViewHolder(binding, onSizeSelected)
    }

    override fun onBindViewHolder(holder: SizeViewHolder, position: Int) {
        val size = sizeItem[position]
        val isSelected = position == selectedPosition
        holder.bind(size, isSelected)
        holder.itemView.setOnClickListener {
            selectedPosition = position
            onSizeSelected(size)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = sizeItem.size

    class SizeViewHolder(
        private val binding: SizeContainerBinding,
        private val onSizeSelected: (Size) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(size: Size, isSelected: Boolean) {
            binding.tvSize.text = size.size
            binding.tvSize.setTextColor(if (size.qty > 0) Color.BLACK else Color.GRAY)
            binding.root.setOnClickListener {
                onSizeSelected(size)
            }
            binding.root.setBackgroundResource(if (isSelected) com.hvuitsme.banzashoes.R.drawable.category_bg_selected else com.hvuitsme.banzashoes.R.drawable.category_bg)
        }
    }
}