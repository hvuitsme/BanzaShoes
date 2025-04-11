package com.hvuitsme.banzashoes.ui.checkout

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.hvuitsme.banzashoes.R
import com.hvuitsme.banzashoes.adapter.ShopListAdapter
import com.hvuitsme.banzashoes.data.model.Address
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

class CheckoutFragment : Fragment() {
    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!

    private var progressDialog: ProgressDialog? = null

    private lateinit var shopListAdapter: ShopListAdapter

    companion object {
        fun newInstance() = CheckoutFragment()
    }

    private lateinit var viewModel: CheckoutViewModel
    private lateinit var addressViewModel: AddressViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cartRepository = CartRepoImpl(CartDataSource(), FirebaseDataSource())
        val checkoutRepository = CheckoutRepoImpl(CheckoutDataSource())
        val checkoutFactory = CheckoutViewModelFactory(cartRepository, checkoutRepository)
        viewModel = ViewModelProvider(this, checkoutFactory).get(CheckoutViewModel::class.java)

        val addressFactory = AddressViewModelFactory(AddressRepoImpl(AddressDataSource()))
        addressViewModel = ViewModelProvider(requireActivity(), addressFactory).get(AddressViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        return inflater.inflate(R.layout.fragment_checkout, container, false)
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.checkoutToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.llAddress.setOnClickListener {
            Log.d("CheckoutFragment", "llAddress clicked, navigating to AddressFragment")
            val navOptions = navOptions {
                anim {
                    enter = R.anim.slide_in_from_right
                    exit = R.anim.slide_out_to_left
                    popEnter = R.anim.pop_slide_in_from_left
                    popExit = R.anim.pop_slide_out_from_right
                }
            }
            findNavController().navigate(R.id.action_checkoutFragment_to_addressFragment, null, navOptions)
        }

        shopListAdapter = ShopListAdapter(emptyList())
        binding.rvShopList.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = shopListAdapter
        }

        viewModel.loadCartDetails()
        viewModel.subtotal.observe(viewLifecycleOwner) { sub ->
            binding.tvSubtotal.text = "$${"%.2f".format(sub)}"
        }
        viewModel.shipping.observe(viewLifecycleOwner) { ship ->
            binding.tvShipping.text = "$${"%.2f".format(ship)}"
        }
        viewModel.total.observe(viewLifecycleOwner) { tot ->
            binding.tvTotal.text = "$${"%.2f".format(tot)}"
        }
        viewModel.cartDisplayItems.observe(viewLifecycleOwner) { items ->
            shopListAdapter.updateItems(items)
        }

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

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                progressDialog = ProgressDialog(context).apply {
                    setMessage("Processing payment...")
                    setCancelable(false)
                    show()
                }
            } else {
                progressDialog?.dismiss()
            }
        }

        binding.btnPayment.setOnClickListener {
            viewModel.processPayment { success ->
                if (success) {
                    Toast.makeText(requireContext(), "Payment Successful", Toast.LENGTH_LONG).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Payment Failed", Toast.LENGTH_LONG).show()
                }
            }
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            addressViewModel.loadAddress(userId)
        } else {
            Log.d("CheckoutFragment", "User not logged in; cannot load address")
        }

        addressViewModel.selectedAddress.observe(viewLifecycleOwner) { selectedAddress ->
            if (selectedAddress != null) {
                updateAddressUI(selectedAddress)
                viewModel.setSelectedAddress(selectedAddress)
            }
        }

        addressViewModel.addressList.observe(viewLifecycleOwner) { addresses ->
            if (addressViewModel.selectedAddress.value == null) {
                addresses.find { it.dfAddress }?.let { defaultAddress ->
                    updateAddressUI(defaultAddress)
                    viewModel.setSelectedAddress(defaultAddress)
                } ?: run {
                    binding.layoutAddressDetails.visibility = View.GONE
                    binding.btnAddAddress.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun updateAddressUI(address: Address) {
        binding.layoutAddressDetails.isVisible = true
        binding.btnAddAddress.isVisible = false
        binding.tvReceiverName.text = address.name
        binding.tvReceiverPhone.text = address.phone
        binding.tvAddressLine1.text = address.address
        binding.tvAddressLine2.text = ""
        Log.d("CheckoutFragment", "UI updated with address: ${address.name}")
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}