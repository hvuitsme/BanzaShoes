package com.hvuitsme.banzashoes.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hvuitsme.banzashoes.adapter.ImageDetailAdapter
import com.hvuitsme.banzashoes.adapter.SizeAdapter
import com.hvuitsme.banzashoes.data.model.Product
import com.hvuitsme.banzashoes.data.remote.CartDataSource
import com.hvuitsme.banzashoes.data.remote.FirebaseDataSource
import com.hvuitsme.banzashoes.data.repository.BanzaRepoImpl
import com.hvuitsme.banzashoes.data.repository.CartRepoImpl
import com.hvuitsme.banzashoes.databinding.FragmentDetailBinding
import com.hvuitsme.banzashoes.ui.cart.CartViewModel
import com.hvuitsme.banzashoes.ui.cart.CartViewModelFactory
import com.hvuitsme.banzashoes.ui.detailBottomSheet.AddToCartBTSDFragment
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
        dotsIndicator.setViewPager2(binding.detailImageContainer)

        viewModel.product.observe(viewLifecycleOwner) { product ->
            product?.let {
                currentProduct = it
                updateUi(it)
            }
        }

        binding.addToCart.setOnClickListener {
            currentProduct?.let { product ->
                val bottomSheet = AddToCartBTSDFragment(product) { selectedSize, qty ->
                    cartViewModel.addOrUpdateCartItem(product.id, qty, selectedSize.size)
                }
                bottomSheet.show(childFragmentManager, "AddToCartBottomSheet")
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