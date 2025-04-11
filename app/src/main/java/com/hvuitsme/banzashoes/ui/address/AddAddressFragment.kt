package com.hvuitsme.banzashoes.ui.address

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.hvuitsme.banzashoes.data.model.Address
import com.hvuitsme.banzashoes.data.remote.AddressDataSource
import com.hvuitsme.banzashoes.data.repository.AddressRepoImpl
import com.hvuitsme.banzashoes.databinding.FragmentAddAddressBinding

class AddAddressFragment : Fragment() {
    private var _binding: FragmentAddAddressBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = AddAddressFragment()
    }

    private lateinit var viewModel: AddressViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = AddressRepoImpl(AddressDataSource())
        val factory = AddressViewModelFactory(repository)
        viewModel = ViewModelProvider(requireActivity(), factory).get(AddressViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        return inflater.inflate(R.layout.fragment_add_address, container, false)
        _binding = FragmentAddAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.selectedAddress.observe(viewLifecycleOwner, Observer { address ->
            if (address != null) {
                binding.tvNewAddress.visibility = View.GONE
                binding.tvEditAddress.visibility = View.VISIBLE
                binding.etFullName.setText(address.name)
                binding.etPhoneNumber.setText(address.phone)
                binding.etProvinceDistrictWard.setText(address.address.split(",").getOrNull(0) ?: "")
                binding.etDetailAddress.setText(address.address.split(",").getOrNull(1) ?: "")
                binding.switchDefault.isChecked = address.dfAddress
            } else {
                binding.tvNewAddress.visibility = View.VISIBLE
                binding.tvEditAddress.visibility = View.GONE
            }
        })

        binding.btnConfirm.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val phone = binding.etPhoneNumber.text.toString().trim()
            val provinceDistrictWard = binding.etProvinceDistrictWard.text.toString().trim()
            val detailAddress = binding.etDetailAddress.text.toString().trim()
            val addressText = "$provinceDistrictWard, $detailAddress"
            val isDefault = binding.switchDefault.isChecked

            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener

            viewModel.selectedAddress.value?.let { oldAddress ->
                val updatedAddress = oldAddress.copy(
                    name = fullName,
                    phone = phone,
                    address = addressText,
                    dfAddress = isDefault,
                    userId = userId
                )
                viewModel.updateAddress(updatedAddress) { success ->
                    if(success) {
                        Toast.makeText(requireContext(), "Updated successfully", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } ?: run {
                val newAddress = Address(
                    userId = userId,
                    name = fullName,
                    phone = phone,
                    address = addressText,
                    dfAddress = isDefault
                )
                viewModel.addAddress(newAddress) { success ->
                    if(success) {
                        Toast.makeText(requireContext(), "Added successfully", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Add failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}