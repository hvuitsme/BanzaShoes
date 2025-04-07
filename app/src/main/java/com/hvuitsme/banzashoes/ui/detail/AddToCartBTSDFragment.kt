package com.hvuitsme.banzashoes.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hvuitsme.banzashoes.adapter.SizeAdapter
import com.hvuitsme.banzashoes.data.model.Product
import com.hvuitsme.banzashoes.data.model.Size
import com.hvuitsme.banzashoes.databinding.FragmentAddToCartBTSDBinding

class AddToCartBTSDFragment(
    private val product: Product,
    private val onAddToCart: (selectedSize: Size, qty: Int) -> Unit
) : BottomSheetDialogFragment() {
    private var _binding: FragmentAddToCartBTSDBinding? = null
    private val binding get() = _binding!!

    private lateinit var sizeAdapter: SizeAdapter
    private var selectedSize: Size? = null
    private var qty: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_add_to_cart_b_t_s_d, container, false)
        _binding = FragmentAddToCartBTSDBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvSizes.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        sizeAdapter = SizeAdapter(product.sizes) { size ->
            selectedSize = size
        }
        binding.rvSizes.adapter = sizeAdapter
        binding.nbQty.text = qty.toString()

        binding.minusBtn.setOnClickListener {
            if (qty > 1) {
                qty--
                binding.nbQty.text = qty.toString()
            }
        }
        binding.plusBtn.setOnClickListener {
            qty++
            binding.nbQty.text = qty.toString()
        }

        binding.btnAddToCart.setOnClickListener {
            if (selectedSize == null) {
                Toast.makeText(context, "Please select size", Toast.LENGTH_SHORT).show()
            } else if (selectedSize?.qty ?: 0 <= 0) {
                Toast.makeText(context, "Out of stock", Toast.LENGTH_SHORT).show()
            } else {
                onAddToCart(selectedSize!!, qty)
                Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}