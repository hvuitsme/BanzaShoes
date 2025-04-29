package com.hvuitsme.banzashoes.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.hvuitsme.banzashoes.data.model.CartDisplayItem
import com.hvuitsme.banzashoes.data.model.Review
import com.hvuitsme.banzashoes.databinding.ItemReviewProductBinding

class ReviewAdapter(
    private val items: List<CartDisplayItem>
) : RecyclerView.Adapter<ReviewAdapter.VH>() {

    private val reviews = MutableList(items.size) { Review("", "", 0.0) }
    private val reviewerName = FirebaseAuth.getInstance().currentUser?.displayName
        ?: FirebaseAuth.getInstance().currentUser?.email.orEmpty()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemReviewProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount() = items.size

    fun getReviews(): List<Pair<CartDisplayItem, Review>> =
        items.mapIndexed { index, item -> item to reviews[index] }

    inner class VH(private val b: ItemReviewProductBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: CartDisplayItem, pos: Int) {
            b.tvProductTitle.text = item.title
            b.tvReviewer.text = reviewerName
            Glide.with(b.ivProduct.context).load(item.imageUrls).into(b.ivProduct)
            b.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
                reviews[pos] = Review(reviewerName, b.etComment.text.toString(), rating.toDouble())
            }
            b.etComment.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    reviews[pos] = Review(reviewerName, s.toString(), b.ratingBar.rating.toDouble())
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }
}
