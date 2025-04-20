package com.hvuitsme.admin.ui.order

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.hvuitsme.admin.R
import com.hvuitsme.admin.adapter.OrderAdapter
import com.hvuitsme.admin.data.remote.OrderDataSource
import com.hvuitsme.admin.data.repository.OrderRepoImpl
import com.hvuitsme.admin.databinding.FragmentOrderBinding

class OrderFragment : Fragment() {
    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: OrderAdapter

    companion object {
        fun newInstance() = OrderFragment()
    }

    private lateinit var viewModel: OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = OrderRepoImpl(OrderDataSource())
        val factory = OrderViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(OrderViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        return inflater.inflate(R.layout.fragment_order, container, false)
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.orderToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        adapter = OrderAdapter(emptyList(), onItemClick = { order ->
            val navOption = navOptions {
                anim {
                    enter = R.anim.slide_in_from_right
                    exit = R.anim.slide_out_to_left
                    popEnter = R.anim.pop_slide_in_from_left
                    popExit = R.anim.pop_slide_out_from_right
                }
            }
            findNavController().navigate(
                R.id.action_orderFragment_to_orderDetailFragment,
                bundleOf("orderId" to order.id),
                navOption
            )
        })
        binding.rvOrder.adapter = adapter
        binding.rvOrder.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        viewModel.orders.observe(viewLifecycleOwner) { orders ->
            adapter.updateData(orders)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}