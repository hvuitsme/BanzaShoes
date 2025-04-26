package com.hvuitsme.banzashoes.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.hvuitsme.banzashoes.R
import com.hvuitsme.banzashoes.adapter.ShopListAdapter
import com.hvuitsme.banzashoes.data.model.Address
import com.hvuitsme.banzashoes.data.remote.OrderDataSource
import com.hvuitsme.banzashoes.data.repository.OrderRepoImpl
import com.hvuitsme.banzashoes.databinding.FragmentOrderDetailBinding

class OrderDetailFragment : Fragment() {
    private var _b: FragmentOrderDetailBinding? = null
    private val b get() = _b!!
    private val vm by viewModels<MyOrderViewModel> {
        MyOrderViewModelFactory(OrderRepoImpl(OrderDataSource()))
    }
    private var currentOrderId: String = ""
    private lateinit var shopAdapter: ShopListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentOrderDetailBinding.inflate(inflater, container, false).also { _b = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        b.detailToolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        currentOrderId = arguments?.getString("orderId") ?: return

        shopAdapter = ShopListAdapter(emptyList())
        b.rvDetailProducts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = shopAdapter
        }

        vm.orders.observe(viewLifecycleOwner) { list ->
            list.find { it.id == currentOrderId }?.let { o ->
                b.tvDetailOrderId.text = "Order ID: ${o.id}"
                b.tvName.text = "Name: ${o.address.name}"
                b.tvDetailAddress.text = "Address: ${o.address.address}"
                b.tvDetailPayment.text = "Payment: ${o.paymentMethod}"
                b.tvStatus.text = "Status: ${o.status}"
                b.tvDetailSummary.text = "Subtotal: ${o.subtotal} | Shipping: ${o.shipping} | Total: ${o.total}"
                b.btnAction1.text = if (o.status in listOf("Pending","Processing")) "Cancel" else "Reorder"
                b.btnAction1.setOnClickListener {
                    if (o.status in listOf("Pending","Processing")) vm.cancelOrder(o)
//                    else vm.reorder(o)
                    else{
                        val cartJson = Gson().toJson(o.cartItems)

                        val bundle = Bundle().apply {
                            putString("cartItems", cartJson)
                            putDouble("subtotal", o.subtotal)
                            putDouble("shipping", o.shipping)
                            putDouble("total", o.total)
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
                            R.id.action_orderDetailFragment2_to_checkoutFragment,
                            bundle,
                            navOptions
                        )
                    }
                }
                shopAdapter.updateItems(o.cartItems)
            }
        }
        vm.loadOnce()

        b.tvDetailAddress.setOnClickListener {
            val navOptions = navOptions {
                anim {
                    enter = R.anim.slide_in_from_right
                    exit = R.anim.slide_out_to_left
                    popEnter = R.anim.pop_slide_in_from_left
                    popExit = R.anim.pop_slide_out_from_right
                }
            }
            findNavController().navigate(R.id.action_orderDetailFragment2_to_addressFragment, null, navOptions)
        }

        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Address>("selectedAddress")
            ?.observe(viewLifecycleOwner) { address ->
                b.tvDetailAddress.text = "Address: ${address.address}"
                vm.changeAddress(currentOrderId, address)
                findNavController().currentBackStackEntry?.savedStateHandle?.remove<Address>("selectedAddress")
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}