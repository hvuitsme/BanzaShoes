package com.hvuitsme.banzashoes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hvuitsme.banzashoes.data.model.Review
import com.hvuitsme.banzashoes.databinding.ItemDisplayReviewBinding

class ReviewDisplayAdapter(
    private val items: List<Review>
) : RecyclerView.Adapter<ReviewDisplayAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemDisplayReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size

    inner class VH(private val b: ItemDisplayReviewBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(r: Review) {
            b.tvReviewerName.text = r.reviewName
            b.ratingBarDisplay.rating = r.rating.toFloat()
            b.tvCommentDisplay.text = r.comment
        }
    }
}