package com.hvuitsme.banzashoes.ui.cart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.hvuitsme.banzashoes.R
import com.hvuitsme.banzashoes.adapter.CartAdapter
import com.hvuitsme.banzashoes.data.remote.CartDataSource
import com.hvuitsme.banzashoes.data.remote.FirebaseDataSource
import com.hvuitsme.banzashoes.data.repository.CartRepoImpl
import com.hvuitsme.banzashoes.databinding.FragmentCartBinding

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = CartFragment()
    }

    private lateinit var viewModel: CartViewModel
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = CartRepoImpl(CartDataSource(), FirebaseDataSource())
        val factory = CartViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[CartViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        return inflater.inflate(R.layout.fragment_cart, container, false)
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cartToolbar.setNavigationOnClickListener {
            val navOptions = navOptions {
                launchSingleTop = true
                restoreState = true
                anim {
                    enter = R.anim.pop_slide_in_from_left
                    exit = R.anim.pop_slide_out_from_right
                    popEnter = R.anim.slide_in_from_right
                    popExit = R.anim.slide_out_to_left
                }
            }
            findNavController().navigateUp()
        }

        cartAdapter = CartAdapter(emptyList()) { productId, newQty ->
            viewModel.updateCartItemQty(productId, newQty)
        }

        binding.rvCart.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCart.adapter = cartAdapter

        viewModel.cartDisplayItems.observe(viewLifecycleOwner) { cartItems ->
            if (cartItems.isEmpty()) {
                binding.rvCart.visibility = View.GONE
                binding.cvBill.visibility = View.GONE
                binding.checkBtn.visibility = View.GONE
                binding.tvEmpty.visibility = View.VISIBLE

                binding.tvSubTt.text = "$0,00"
                binding.tvShip.text = "$0,00"
                binding.tvTotal.text = "$0,00"
            } else {
                binding.rvCart.visibility = View.VISIBLE
                binding.cvBill.visibility = View.VISIBLE
                binding.checkBtn.visibility = View.VISIBLE
                binding.tvEmpty.visibility = View.GONE

                cartAdapter.updateDataCart(cartItems)

                var subtotal = 0.0
                cartItems.forEach { cartItem ->
                    subtotal += cartItem.price * cartItem.quantity
                }
                val shiping = if (subtotal > 0) 10.0 else 0.0
                val total = subtotal + shiping
                binding.tvSubTt.text = "$${"%.2f".format(subtotal)}"
                binding.tvShip.text = "$${"%.2f".format(shiping)}"
                binding.tvTotal.text = "$${"%.2f".format(total)}"
            }
        }

        viewModel.loadCartItems()

        binding.checkBtn.setOnClickListener {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}