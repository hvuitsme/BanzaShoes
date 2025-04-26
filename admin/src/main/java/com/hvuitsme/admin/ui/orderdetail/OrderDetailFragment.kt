package com.hvuitsme.admin.ui.orderdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hvuitsme.admin.adapter.OrderDetailAdapter
import com.hvuitsme.admin.data.remote.OrderDataSource
import com.hvuitsme.admin.data.repository.OrderRepoImpl
import com.hvuitsme.admin.databinding.FragmentOrderDetailAdminBinding
import com.hvuitsme.admin.ui.order.OrderViewModel
import com.hvuitsme.admin.ui.order.OrderViewModelFactory

class OrderDetailFragment : Fragment() {

    private var _binding: FragmentOrderDetailAdminBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: OrderViewModel
    private lateinit var detailAdapter: OrderDetailAdapter
    private val statuses = listOf("Pending", "Processing", "Shipping", "Success", "Cancelled")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo = OrderRepoImpl(OrderDataSource())
        val factory = OrderViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory).get(OrderViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderDetailAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbarDetail.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        detailAdapter = OrderDetailAdapter(emptyList())
        binding.rvDetailItems.apply {
            adapter = detailAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            statuses
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStatusDetail.adapter = spinnerAdapter

        val orderId = arguments?.getString("orderId") ?: return

        viewModel.orders.observe(viewLifecycleOwner) { list ->
            val order = list.find { it.id == orderId } ?: return@observe
            binding.tvDetailOrderId.text = "Order ID: ${order.id}"
            binding.tvDetailPayment.text = "Payment: ${order.paymentMethod}"
            binding.tvDetailAddress.text = "Address: ${order.address.address}"
            binding.tvDetailSummary.text =
                "Subtotal: ${order.subtotal} | Shipping: ${order.shipping} | Total: ${order.total}"
            detailAdapter.updateData(order.cartItems)
            statuses.indexOf(order.status).takeIf { it >= 0 }?.let {
                binding.spinnerStatusDetail.setSelection(it)
            }
        }

        binding.spinnerStatusDetail.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    val newStatus = statuses[position]
                    val current = viewModel.orders.value?.find { it.id == orderId } ?: return
                    if (newStatus != current.status) {
                        viewModel.updateOrderStatus(current, newStatus)
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}