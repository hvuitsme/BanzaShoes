package com.hvuitsme.banzashoes.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hvuitsme.banzashoes.R
import com.hvuitsme.banzashoes.data.remote.OrderDataSource
import com.hvuitsme.banzashoes.data.repository.OrderRepoImpl
import com.hvuitsme.banzashoes.databinding.FragmentOrderListBinding
import androidx.navigation.navOptions
import com.google.gson.Gson

class OrderListFragment : Fragment() {

    companion object {
        private const val ARG_STATUS = "status"
        fun newInstance(status: String) = OrderListFragment().apply {
            arguments = Bundle().apply { putString(ARG_STATUS, status) }
        }
    }

    private var _binding: FragmentOrderListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: OrderAdapter
    private val statusFilter by lazy { arguments?.getString(ARG_STATUS) ?: "" }
    private lateinit var viewModel: MyOrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = OrderRepoImpl(OrderDataSource())
        val factory = MyOrderViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MyOrderViewModel::class]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrderListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = OrderAdapter(
            onItemClicked = { order ->
                val navOptions = navOptions {
                    anim {
                        enter = R.anim.slide_in_from_right
                        exit = R.anim.slide_out_to_left
                        popEnter = R.anim.pop_slide_in_from_left
                        popExit = R.anim.pop_slide_out_from_right
                    }
                }
                findNavController().navigate(
                    R.id.action_myOrderFragment_to_orderDetailFragment2,
                    Bundle().apply { putString("orderId", order.id) },
                    navOptions
                )
            },
            onAction = { order, action ->
                when (action) {
                    OrderAdapter.Action.CANCEL -> viewModel.cancelOrder(order)
                    OrderAdapter.Action.REORDER -> {
                        val cartJson = Gson().toJson(order.cartItems)
                        val subtotal = order.subtotal
                        val shipping = order.shipping
                        val total    = order.total

                        val bundle = Bundle().apply {
                            putString("cartItems", cartJson)
                            putDouble("subtotal", subtotal)
                            putDouble("shipping", shipping)
                            putDouble("total", total)
                        }

                        val navOptions = navOptions {
                            anim {
                                enter = R.anim.slide_in_from_right
                                exit = R.anim.slide_out_to_left
                                popEnter = R.anim.pop_slide_in_from_left
                                popExit = R.anim.pop_slide_out_from_right
                            }
                        }
                        findNavController().navigate(
                            R.id.action_myOrderFragment_to_checkoutFragment,
                            bundle,
                            navOptions
                        )

                    }
                    OrderAdapter.Action.CHANGE_ADDRESS -> TODO()
                }
            }
        )
        binding.rvOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrders.adapter = adapter
        binding.rvOrders.isClickable = false

        viewModel.orders.observe(viewLifecycleOwner, Observer { list ->
            val filtered = list.filter { it.status == statusFilter }
            adapter.submitList(filtered)
            val empty = filtered.isEmpty()
            binding.rvOrders.visibility = if (empty) View.GONE else View.VISIBLE
            binding.tvEmpty.visibility = if (empty) View.VISIBLE else View.GONE
        })

        viewModel.loadOnce()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}