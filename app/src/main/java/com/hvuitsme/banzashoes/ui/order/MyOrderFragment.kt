package com.hvuitsme.banzashoes.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.hvuitsme.banzashoes.databinding.FragmentMyOrderBinding

class MyOrderFragment : Fragment() {

    private var _binding: FragmentMyOrderBinding? = null
    private val binding get() = _binding!!
    private val statuses = listOf("Pending", "Processing", "Shipping", "Success", "Cancelled")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMyOrderBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // back button
        binding.orderToolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        // set adapter
        binding.viewPager.adapter = OrderPagerAdapter(requireActivity(), statuses)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            tab.text = statuses[pos]
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}