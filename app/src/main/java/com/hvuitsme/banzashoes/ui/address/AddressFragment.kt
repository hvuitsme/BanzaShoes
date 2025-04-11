package com.hvuitsme.banzashoes.ui.address

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hvuitsme.banzashoes.R
import com.hvuitsme.banzashoes.adapter.AddressAdapter
import com.hvuitsme.banzashoes.data.remote.AddressDataSource
import com.hvuitsme.banzashoes.data.repository.AddressRepoImpl
import com.hvuitsme.banzashoes.databinding.FragmentAddressBinding

class AddressFragment : Fragment() {

    private var _binding: FragmentAddressBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AddressAdapter

    companion object {
        fun newInstance() = AddressFragment()
    }

    private lateinit var viewModel: AddressViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = AddressRepoImpl(AddressDataSource())
        val factory = AddressViewModelFactory(repository)
        viewModel = ViewModelProvider(requireActivity(), factory).get(AddressViewModel::class.java)
        Log.d("AddressFragment", "AddressViewModel initialized")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        return inflater.inflate(R.layout.fragment_address, container, false)
        _binding = FragmentAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chooseAddressToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        adapter = AddressAdapter(
            addressItems = emptyList(),
            onSelectClick = { address ->
                Log.d("AddressFragment", "Address selected: $address")
                viewModel.setSelectedAddress(address)
                findNavController().popBackStack()
            },
            onAddClick = {
                Log.d("AddressFragment", "Add Address button clicked, navigating to AddAddressFragment")
                viewModel.setSelectedAddress(null)
                findNavController().navigate(R.id.action_addressFragment_to_addAddressFragment)
            },
            onEditClick = { address ->
                Log.d("AddressFragment", "Edit Address clicked: $address")
                viewModel.setSelectedAddress(address)
                findNavController().navigate(R.id.action_addressFragment_to_addAddressFragment)
            },
            onDeleteClick = { address ->
                Log.d("AddressFragment", "Delete Address clicked: $address")
                viewModel.deleteAddress(address) { success ->
                    Log.d("AddressFragment", "Delete Address result: $success")
                }
            }
        )

        binding.rvAddress.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvAddress.adapter = adapter

        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            Log.d("AddressFragment", "Loading addresses for user: $userId")
            viewModel.loadAddress(userId)
        } else {
            Log.d("AddressFragment", "User not logged in")
        }

        viewModel.addressList.observe(viewLifecycleOwner) { addresses ->
            Log.d("AddressFragment", "Address list updated, size: ${addresses.size}")
            adapter.updateDataAddress(addresses)
            binding.tvNoAddress.visibility = if (addresses.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}