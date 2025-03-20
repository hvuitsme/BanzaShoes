package com.hvuitsme.banzashoes.ui.home

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.hvuitsme.banzashoes.R
import com.hvuitsme.banzashoes.adapter.CarouselAdapter
import com.hvuitsme.banzashoes.adapter.CategoryAdapter
import com.hvuitsme.banzashoes.adapter.ProductAdapter
import com.hvuitsme.banzashoes.data.model.Product
import com.hvuitsme.banzashoes.data.remote.CartDataSource
import com.hvuitsme.banzashoes.data.remote.FirebaseDataSource
import com.hvuitsme.banzashoes.data.repository.BanzaRepoImpl
import com.hvuitsme.banzashoes.data.repository.CartRepoImpl
import com.hvuitsme.banzashoes.databinding.FragmentHomeBinding
import com.hvuitsme.banzashoes.databinding.NavHeaderBinding
import com.hvuitsme.banzashoes.ui.cart.CartViewModel
import com.hvuitsme.banzashoes.ui.cart.CartViewModelFactory
import com.hvuitsme.test2.GoogleAuthClient
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val autoScrollDelay = 4000L
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var headerBinding: NavHeaderBinding
    private lateinit var googleAuthClinet: GoogleAuthClient

    private lateinit var carousel: ViewPager2
    private lateinit var category: RecyclerView
    private lateinit var product: RecyclerView

    private lateinit var carouselAdapter: CarouselAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var productAdapter: ProductAdapter

    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            carousel.setCurrentItem(carousel.currentItem + 1, true)
            handler.postDelayed(this, autoScrollDelay)
        }
    }

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel
    private lateinit var cartViewModel: CartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val databaseSource = FirebaseDataSource()
        val repository = BanzaRepoImpl(FirebaseDataSource())
        val factory = HomeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        val cartRepository = CartRepoImpl(CartDataSource(), FirebaseDataSource())
        val cartFactory = CartViewModelFactory(cartRepository)
        cartViewModel = ViewModelProvider(this, cartFactory)[CartViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        return inflater.inflate(R.layout.fragment_home, container, false)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeSwipeRefresh.setOnRefreshListener {

            binding.homeSwipeRefresh.isRefreshing = false

            handler.postDelayed({
            }, 2000)
        }

        googleAuthClinet = GoogleAuthClient(requireContext())

        val navHeader = binding.navView.getHeaderView(0)

        headerBinding = NavHeaderBinding.bind(navHeader)

        updateUi()

        navHeader.setOnClickListener {

            if (!googleAuthClinet.isSingedIn()) {
                binding.homeDrawLayout.closeDrawers()

                val navOptions = navOptions {
                    launchSingleTop = true
                    restoreState = true
                    anim {
                        enter = R.anim.slide_in_from_right
                        exit = R.anim.slide_out_to_left
                        popEnter = R.anim.pop_slide_in_from_left
                        popExit = R.anim.pop_slide_out_from_right
                    }
                }
                findNavController().navigate(
                    R.id.action_homeFragment_to_signinFragment,
                    null,
                    navOptions
                )
            } else {
//                binding.homeDrawLayout.closeDrawers()
            }
        }

        binding.llSignout.setOnClickListener {
            handler.postDelayed({
                lifecycleScope.launch {
                    if (googleAuthClinet.isSingedIn()) {
                        googleAuthClinet.signOut()
                        updateUi()
                    }
                }
            }, 2000)
        }

        binding.topAppBar.setNavigationOnClickListener {
            binding.homeDrawLayout.openDrawer(GravityCompat.START)
        }

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val logoRes = if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            R.drawable.logo_banza_invert
        } else {
            R.drawable.logo_banza
        }

        binding.topAppBar.findViewById<ImageView>(R.id.logo_home)?.setImageResource(logoRes)

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.cart_toolbar -> {
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val navOptions = navOptions {
                        launchSingleTop = true
                        restoreState = true
                        anim {
                            enter = R.anim.slide_in_from_right
                            exit = R.anim.slide_out_to_left
                            popEnter = R.anim.pop_slide_in_from_left
                            popExit = R.anim.pop_slide_out_from_right
                        }
                    }
                    if (currentUser != null || googleAuthClinet.isSingedIn()) {
                        findNavController().navigate(
                            R.id.action_homeFragment_to_cartFragment,
                            null,
                            navOptions
                        )
                    } else {
                        findNavController().navigate(
                            R.id.action_homeFragment_to_signinFragment,
                            null,
                            navOptions
                        )
                    }
                    true
                }

                else -> false
            }
        }

        carousel = binding.carouselViewpager2
        carouselAdapter = CarouselAdapter(emptyList())
        carousel.adapter = carouselAdapter

        carousel.setPageTransformer { page, position ->
            page.alpha = 1 - kotlin.math.abs(position)
            page.translationX = -position * page.width
            val scale = 0.85f + (1 - kotlin.math.abs(position)) * 0.15f
            page.scaleX = scale
            page.scaleY = scale
        }

        viewModel.loadCarousel()

        viewModel.carousel.observe(viewLifecycleOwner) { carouselList ->
            if (carouselList.isNotEmpty()) {
                carouselAdapter.updateDataCarousel(carouselList)

                val startPosition = Int.MAX_VALUE / 2 - (Int.MAX_VALUE / 2) % carouselList.size
                carousel.setCurrentItem(startPosition, false)
                handler.removeCallbacks(autoScrollRunnable)
                handler.postDelayed(autoScrollRunnable, autoScrollDelay)
            }
        }

        category = binding.rvCategory
        categoryAdapter = CategoryAdapter(emptyList())
        category.adapter = categoryAdapter

        category.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        category.isNestedScrollingEnabled = false

        viewModel.loadCategory()

        viewModel.category.observe(viewLifecycleOwner) { categoryList ->
            categoryAdapter.updateDataCategory(categoryList)
            categoryAdapter.setSelectedPosition(0)
            if (categoryList.isNotEmpty()) {
                filterProductByCategory(categoryList[0].cateId)
                if (viewModel.product.value.isNullOrEmpty()){
                    viewModel.loadProduct(categoryList[0].cateId)
                }
            }
        }

        categoryAdapter.setOnCategoryClick { categoryItem, position ->
            categoryAdapter.setSelectedPosition(position)
            filterProductByCategory(categoryItem.cateId)
            viewModel.loadProduct(categoryItem.cateId)
        }

        product = binding.rvRecommend
        productAdapter = ProductAdapter(emptyList()){product ->
            cartViewModel.addOrUpdateCartItem(product.id)
        }
        product.adapter = productAdapter

        product.layoutManager = GridLayoutManager(requireContext(), 2)
        product.isNestedScrollingEnabled = false
        product.isSaveEnabled = true

        viewModel.loadProduct()

        viewModel.product.observe(viewLifecycleOwner) { productList ->
            productAdapter.updateDataProduct(productList)
        }
    }

    private fun updateUi() {
        val llSignout = binding.llSignout
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            headerBinding.tvName.visibility = View.VISIBLE
            headerBinding.tvName.text = currentUser.displayName ?: "User"
            headerBinding.tvEmail.visibility = View.VISIBLE
            headerBinding.tvEmail.text = currentUser.email
            Glide.with(this)
                .load(currentUser.photoUrl)
                .placeholder(R.drawable.ic_guest)
                .into(headerBinding.avatar)
            llSignout.visibility = View.VISIBLE
            headerBinding.tvLogin.visibility = View.GONE
        } else {
            headerBinding.tvName.visibility = View.GONE
            headerBinding.tvEmail.visibility = View.GONE
            headerBinding.avatar.setImageResource(R.drawable.ic_guest)
            llSignout.visibility = View.GONE
            headerBinding.tvLogin.visibility = View.VISIBLE
        }
    }

    private fun filterProductByCategory(cateId: String) {
        val fullProductList = viewModel.product.value ?: emptyList()
        val filteredList = fullProductList.filter { it.cateId == cateId }
        productAdapter.updateDataProduct(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}