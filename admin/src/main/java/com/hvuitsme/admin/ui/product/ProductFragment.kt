package com.hvuitsme.admin.ui.product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.hvuitsme.admin.R
import com.hvuitsme.admin.adapter.BrandPagerAdapter
import com.hvuitsme.admin.data.remote.CategoryDataSource
import com.hvuitsme.admin.data.remote.ProductDataSource
import com.hvuitsme.admin.data.repository.CategoryRepoImpl
import com.hvuitsme.admin.data.repository.ProductRepoImpl
import com.hvuitsme.admin.databinding.FragmentProductBinding
import com.hvuitsme.admin.ui.category.CategoryViewModel
import com.hvuitsme.admin.ui.category.CategoryViewModelFactory

class ProductFragment : Fragment() {
    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = ProductFragment()
    }

    private lateinit var viewModel: ProductViewModel
    private lateinit var categoryViewModel: CategoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = ProductRepoImpl(ProductDataSource())
        val factory = ProductViewModelFactory(repository, CategoryRepoImpl(CategoryDataSource()))
        viewModel = ViewModelProvider(requireActivity(), factory)[ProductViewModel::class.java]
        viewModel.loadProducts()

        val categoryRepository = CategoryRepoImpl(CategoryDataSource())
        val categoryFactory = CategoryViewModelFactory(categoryRepository)
        categoryViewModel = ViewModelProvider(requireActivity(), categoryFactory)[CategoryViewModel::class.java]
        categoryViewModel.loadCategories()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        return inflater.inflate(R.layout.fragment_product, container, false)
        _binding = FragmentProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.productToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        categoryViewModel.categories.observe(viewLifecycleOwner) { categoryList ->
            val cateIds = categoryList.map { it.cateId }
            val pagerAdapter = BrandPagerAdapter(this, cateIds)
            binding.vpProduct.adapter = pagerAdapter

            TabLayoutMediator(binding.tlBrand, binding.vpProduct) { tab, position ->
                val tabView = layoutInflater.inflate(R.layout.item_tab_brand, null)
                val ivBrand = tabView.findViewById<ImageView>(R.id.ivBrand)
                val categories = categoryViewModel.categories.value ?: emptyList()
                if (position < categories.size) {
                    val url = categories[position].url
                    Glide.with(tabView.context).load(url).into(ivBrand)
                }
                tab.customView = tabView
            }.attach()
        }
        binding.vpProduct.isUserInputEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}