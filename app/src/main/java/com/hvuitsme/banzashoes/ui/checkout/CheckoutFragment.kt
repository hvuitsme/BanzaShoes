package com.hvuitsme.banzashoes.ui.checkout

import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hvuitsme.banzashoes.R
import com.hvuitsme.banzashoes.adapter.ShopListAdapter
import com.hvuitsme.banzashoes.data.model.Address
import com.hvuitsme.banzashoes.data.model.CartDisplayItem
import com.hvuitsme.banzashoes.data.remote.AddressDataSource
import com.hvuitsme.banzashoes.data.remote.CartDataSource
import com.hvuitsme.banzashoes.data.remote.CheckoutDataSource
import com.hvuitsme.banzashoes.data.remote.FirebaseDataSource
import com.hvuitsme.banzashoes.data.repository.AddressRepoImpl
import com.hvuitsme.banzashoes.data.repository.CartRepoImpl
import com.hvuitsme.banzashoes.data.repository.CheckoutRepoImpl
import com.hvuitsme.banzashoes.databinding.FragmentCheckoutBinding
import com.hvuitsme.banzashoes.ui.address.AddressViewModel
import com.hvuitsme.banzashoes.ui.address.AddressViewModelFactory
import vn.zalopay.sdk.ZaloPaySDK
import vn.zalopay.sdk.listeners.PayOrderListener
import vn.zalopay.sdk.ZaloPayError

class CheckoutFragment : Fragment() {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!
    private var progressDialog: ProgressDialog? = null
    private lateinit var shopListAdapter: ShopListAdapter
    private lateinit var viewModel: CheckoutViewModel
    private lateinit var addressViewModel: AddressViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cartRepo = CartRepoImpl(CartDataSource(), FirebaseDataSource())
        val checkoutRepo = CheckoutRepoImpl(CheckoutDataSource())
        viewModel = ViewModelProvider(this, CheckoutViewModelFactory(cartRepo, checkoutRepo))[CheckoutViewModel::class.java]
        val addressFactory = AddressViewModelFactory(AddressRepoImpl(AddressDataSource()))
        addressViewModel = ViewModelProvider(requireActivity(), addressFactory)[AddressViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.checkoutToolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.llAddress.setOnClickListener {
            val opts = navOptions {
                anim {
                    enter = R.anim.slide_in_from_right
                    exit = R.anim.slide_out_to_left
                    popEnter = R.anim.pop_slide_in_from_left
                    popExit = R.anim.pop_slide_out_from_right
                }
            }
            findNavController().navigate(R.id.action_checkoutFragment_to_addressFragment, null, opts)
        }

        shopListAdapter = ShopListAdapter(emptyList())
        binding.rvShopList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = shopListAdapter
        }

        val args = arguments
        if (args?.containsKey("cartItems") == true) {
            val cartJson = args.getString("cartItems")!!
            val type = object : TypeToken<List<CartDisplayItem>>() {}.type
            val items: List<CartDisplayItem> = Gson().fromJson(cartJson, type)
            shopListAdapter.updateItems(items)
            viewModel.cartDisplayItems.value = items
            val sub = args.getDouble("subtotal", 0.0)
            val ship = args.getDouble("shipping", 0.0)
            val tot = args.getDouble("total", 0.0)
            binding.tvSubtotal.text = "$${"%.2f".format(sub)}"
            binding.tvShipping.text = "$${"%.2f".format(ship)}"
            binding.tvTotal.text = "$${"%.2f".format(tot)}"
            viewModel.subtotal.value = sub
            viewModel.shipping.value = ship
            viewModel.total.value = tot
        } else {
            viewModel.loadCartDetails()
        }

        viewModel.subtotal.observe(viewLifecycleOwner) { binding.tvSubtotal.text = "$${"%.2f".format(it)}" }
        viewModel.shipping.observe(viewLifecycleOwner) { binding.tvShipping.text = "$${"%.2f".format(it)}" }
        viewModel.total.observe(viewLifecycleOwner) { binding.tvTotal.text = "$${"%.2f".format(it)}" }
        viewModel.cartDisplayItems.observe(viewLifecycleOwner) { shopListAdapter.updateItems(it) }

        viewModel.setPaymentMethod("ZaloPay")
        binding.radioZaloPay.isChecked = true
        binding.radioMomo.isChecked = false
        binding.radioCOD.isChecked = false

        binding.cardZaloPay.setOnClickListener {
            viewModel.setPaymentMethod("ZaloPay")
            binding.radioZaloPay.isChecked = true
            binding.radioMomo.isChecked = false
            binding.radioCOD.isChecked = false
        }
        binding.cardMomo.setOnClickListener {
            viewModel.setPaymentMethod("Momo")
            binding.radioZaloPay.isChecked = false
            binding.radioMomo.isChecked = true
            binding.radioCOD.isChecked = false
        }
        binding.cardCOD.setOnClickListener {
            viewModel.setPaymentMethod("COD")
            binding.radioZaloPay.isChecked = false
            binding.radioMomo.isChecked = false
            binding.radioCOD.isChecked = true
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            if (it) {
                progressDialog = ProgressDialog(context).apply {
                    setMessage("Processing payment...")
                    setCancelable(false)
                    show()
                }
            } else {
                progressDialog?.dismiss()
            }
        }

        viewModel.paymentToken.observe(viewLifecycleOwner) { token ->
            if (token.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Failed to create ZaloPay order", Toast.LENGTH_LONG).show()
                return@observe
            }
            ZaloPaySDK.getInstance().payOrder(
                requireActivity(),
                token,
                "banzashoes://app",
                object : PayOrderListener {
                    override fun onPaymentSucceeded(transactionId: String?, transToken: String?, appTransId: String?) {
                        viewModel.processPayment { ok ->
                            if (ok) {
                                Toast.makeText(requireContext(), "Payment successful", Toast.LENGTH_LONG).show()
                                findNavController().popBackStack()
                            } else {
                                Toast.makeText(requireContext(), "Payment failed", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    override fun onPaymentCanceled(transactionId: String?, transToken: String?) {
                        Toast.makeText(requireContext(), "Payment cancelled", Toast.LENGTH_LONG).show()
                    }
                    override fun onPaymentError(error: ZaloPayError?, transactionId: String?, errorMessage: String?) {
                        Toast.makeText(requireContext(), "Payment error", Toast.LENGTH_LONG).show()
                    }
                }
            )
        }

        binding.btnPayment.setOnClickListener {
            when (viewModel._paymentMethod.value) {
                "ZaloPay" -> viewModel.createZaloPayOrder()
                "Momo" -> { }
                else -> viewModel.processPayment { ok ->
                    if (ok) {
                        Toast.makeText(requireContext(), "Payment successful", Toast.LENGTH_LONG).show()
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Payment failed", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        FirebaseAuth.getInstance().currentUser?.uid?.let { addressViewModel.loadAddress(it) }
        addressViewModel.selectedAddress.observe(viewLifecycleOwner) {
            it?.let { a ->
                binding.layoutAddressDetails.isVisible = true
                binding.btnAddAddress.isVisible = false
                binding.tvReceiverName.text = a.name
                binding.tvReceiverPhone.text = a.phone
                binding.tvAddressLine1.text = a.address
                viewModel.setSelectedAddress(a)
            }
        }
        addressViewModel.addressList.observe(viewLifecycleOwner) { list ->
            if (addressViewModel.selectedAddress.value == null) {
                list.find { it.dfAddress }?.let { d ->
                    binding.layoutAddressDetails.isVisible = true
                    binding.btnAddAddress.isVisible = false
                    binding.tvReceiverName.text = d.name
                    binding.tvReceiverPhone.text = d.phone
                    binding.tvAddressLine1.text = d.address
                    viewModel.setSelectedAddress(d)
                } ?: run {
                    binding.layoutAddressDetails.visibility = View.GONE
                    binding.btnAddAddress.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        addressViewModel.setSelectedAddress(null)
        _binding = null
    }
}