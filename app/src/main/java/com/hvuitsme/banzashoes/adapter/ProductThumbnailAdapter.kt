package com.hvuitsme.banzashoes.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hvuitsme.banzashoes.data.model.CartDisplayItem
import com.hvuitsme.banzashoes.databinding.ItemProductThumbnailBinding

class ProductThumbAdapter(
    private val items: List<CartDisplayItem>
) : RecyclerView.Adapter<ProductThumbAdapter.VH>() {

    inner class VH(val b: ItemProductThumbnailBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemProductThumbnailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val it = items[position]
        Glide.with(holder.b.ivThumb.context)
            .load(it.imageUrls)
            .into(holder.b.ivThumb)
    }
}