package com.hvuitsme.banzashoes.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.hvuitsme.banzashoes.R
import com.hvuitsme.banzashoes.adapter.ImageDetailAdapter
import com.hvuitsme.banzashoes.adapter.SizeAdapter
import com.hvuitsme.banzashoes.data.model.CartDisplayItem
import com.hvuitsme.banzashoes.data.model.Product
import com.hvuitsme.banzashoes.data.remote.CartDataSource
import com.hvuitsme.banzashoes.data.remote.FirebaseDataSource
import com.hvuitsme.banzashoes.data.repository.BanzaRepoImpl
import com.hvuitsme.banzashoes.data.repository.CartRepoImpl
import com.hvuitsme.banzashoes.databinding.FragmentDetailBinding
import com.hvuitsme.banzashoes.service.GoogleAuthClient
import com.hvuitsme.banzashoes.ui.cart.CartViewModel
import com.hvuitsme.banzashoes.ui.cart.CartViewModelFactory
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = DetailFragment()
    }

    private lateinit var viewModel: DetailViewModel
    private lateinit var cartViewModel: CartViewModel

    private lateinit var imageDetailAdapter: ImageDetailAdapter
    private lateinit var googleAuthClinet: GoogleAuthClient

    private var currentProduct: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = BanzaRepoImpl(FirebaseDataSource())
        val factory = DetailViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]

        val cartRepository = CartRepoImpl(CartDataSource(), FirebaseDataSource())
        val cartFactory = CartViewModelFactory(cartRepository)
        cartViewModel = ViewModelProvider(this, cartFactory)[CartViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        return inflater.inflate(R.layout.fragment_detail, container, false)
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        googleAuthClinet = GoogleAuthClient(requireContext())

        binding.detailToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        val productId = arguments?.getString("productId")
        productId?.let {
            viewModel.loadDetailProduct(it)
        }

        imageDetailAdapter = ImageDetailAdapter()
        binding.detailImageContainer.adapter = imageDetailAdapter

        val dotsIndicator: WormDotsIndicator = binding.dotsIndicator
        dotsIndicator.attachTo(binding.detailImageContainer)

        viewModel.product.observe(viewLifecycleOwner) { product ->
            product?.let {
                currentProduct = it
                updateUi(it)
            }
        }

        val navOptions = navOptions {
            anim {
                enter = R.anim.slide_in_from_right
                exit = R.anim.slide_out_to_left
                popEnter = R.anim.pop_slide_in_from_left
                popExit = R.anim.pop_slide_out_from_right
            }
        }

        binding.addToCart.setOnClickListener {
            currentProduct?.let { product ->
                if (FirebaseAuth.getInstance().currentUser != null || googleAuthClinet.isSingedIn()) {
                    val bottomSheet = AddToCartBTSDFragment(product) { selectedSize, qty ->
                        cartViewModel.addOrUpdateCartItem(product.id, qty, selectedSize.size)
                    }
                    bottomSheet.show(childFragmentManager, "AddToCartBottomSheet")
                }else{
                    findNavController().navigate(R.id.action_detailFragment_to_signinFragment, null, navOptions)
                }
            }
        }

        binding.buyBtn.setOnClickListener {
            currentProduct?.let { product ->
                if (FirebaseAuth.getInstance().currentUser != null ||
                    googleAuthClinet.isSingedIn()
                ) {
                    val bottomSheet = AddToCartBTSDFragment(product) { selectedSize, qty ->
                        val cartItem = CartDisplayItem(
                            productId   = product.id,
                            title       = product.title,
                            price       = product.price,
                            size        = selectedSize.size,
                            quantity    = qty,
                            imageUrls    = product.imageUrls.firstOrNull().orEmpty()
                        )

                        val cartJson = Gson().toJson(listOf(cartItem))

                        val subtotal = cartItem.price * cartItem.quantity
                        val shipping = 0.0
                        val total    = subtotal + shipping

                        val bundle = Bundle().apply {
                            putString("cartItems", cartJson)
                            putDouble("subtotal", subtotal)
                            putDouble("shipping", shipping)
                            putDouble("total", total)
                        }

                        findNavController().navigate(
                            R.id.action_detailFragment_to_checkoutFragment,
                            bundle,
                            navOptions
                        )
                    }

                    bottomSheet.show(childFragmentManager, "AddToCartBottomSheet")
                } else {
                    findNavController().navigate(
                        R.id.action_detailFragment_to_signinFragment,
                        null,
                        navOptions
                    )
                }
            }
        }
    }

    private fun updateUi(product: Product) {
        binding.detailTitle.text = product.title
        binding.tvDescription.text = product.description
        binding.tvPrice.text = "$${product.price}"
        binding.ivRating.text = "${product.rating}/5"
        imageDetailAdapter.updateDataImage(product.imageUrls)
    }
}