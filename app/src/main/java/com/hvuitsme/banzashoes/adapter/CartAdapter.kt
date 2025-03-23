package com.hvuitsme.banzashoes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hvuitsme.banzashoes.data.model.Cart
import com.hvuitsme.banzashoes.data.model.CartDisplayItem
import com.hvuitsme.banzashoes.databinding.CartContainerBinding

class CartAdapter(
    private var cartItems: List<CartDisplayItem>,
    private val onQtyChange: (productId: String, newQty: Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHoler>() {

    fun updateDataCart(cartItems: List<CartDisplayItem>) {
        this.cartItems = cartItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartViewHoler {
        val binding = CartContainerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CartViewHoler(binding, onQtyChange)
    }

    override fun onBindViewHolder(holder: CartViewHoler, position: Int) {
        holder.bind(cartItems[position])
    }

    override fun getItemCount(): Int = cartItems.size

    class CartViewHoler(
        private val binding: CartContainerBinding,
        private val onQtyChange: (productId: String, newQty: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cartItem: CartDisplayItem) {
            binding.tvCart.text = cartItem.title
            binding.tvCartPrice.text = "$${cartItem.price}"
            binding.tvCartSize.text = "Size: ${cartItem.size}"
            binding.nbQty.text = cartItem.quantity.toString()
            Glide
                .with(binding.root.context)
                .load(cartItem.imageUrls)
                .into(binding.ivCart)

            binding.minusBtn.setOnClickListener {
                if (cartItem.quantity > 0) {
                    val newQty = cartItem.quantity - 1
                    binding.nbQty.text = newQty.toString()
                    onQtyChange(cartItem.productId, newQty)
                }
            }

            binding.plusBtn.setOnClickListener {
                val newQty = cartItem.quantity + 1
                binding.nbQty.text = newQty.toString()
                onQtyChange(cartItem.productId, newQty)
            }
        }
    }
}