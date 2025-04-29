package com.hvuitsme.banzashoes.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hvuitsme.banzashoes.adapter.ReviewAdapter
import com.hvuitsme.banzashoes.data.model.CartDisplayItem
import com.hvuitsme.banzashoes.data.model.Review
import com.hvuitsme.banzashoes.databinding.FragmentReviewBinding
import com.hvuitsme.banzashoes.data.remote.OrderDataSource
import com.hvuitsme.banzashoes.data.repository.OrderRepoImpl

class ReviewFragment : Fragment() {

    private var _b: FragmentReviewBinding? = null
    private val b get() = _b!!
    private val vm by viewModels<MyOrderViewModel> { MyOrderViewModelFactory(OrderRepoImpl(OrderDataSource())) }
    private lateinit var adapter: ReviewAdapter
    private lateinit var orderId: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentReviewBinding.inflate(inflater, container, false).also { _b = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        b.reviewToolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        orderId = requireArguments().getString("orderId")!!
        val json = requireArguments().getString("cartItems")!!
        val items: List<CartDisplayItem> = Gson().fromJson(json, object : TypeToken<List<CartDisplayItem>>() {}.type)
        adapter = ReviewAdapter(items)
        b.rvReviews.layoutManager = LinearLayoutManager(requireContext())
        b.rvReviews.adapter = adapter
        vm.reviewSubmitted.observe(viewLifecycleOwner, Observer { done ->
            if (done) {
                Toast.makeText(requireContext(), "Review sent", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        })

        b.btnSubmit.setOnClickListener {
            val reviews = adapter.getReviews().map { it.first.productId to it.second }
            vm.submitReviews(orderId, reviews)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}