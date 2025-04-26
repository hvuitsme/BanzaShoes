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
    private lateinit var viewModel: AddressViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo = AddressRepoImpl(AddressDataSource())
        val factory = AddressViewModelFactory(repo)
        viewModel = ViewModelProvider(requireActivity(), factory).get(AddressViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentAddressBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.chooseAddressToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        adapter = AddressAdapter(
            addressItems = emptyList(),
            onSelectClick = { address ->
                viewModel.setSelectedAddress(address)
                findNavController().popBackStack()
            },
            onAddClick = {
                viewModel.setSelectedAddress(null)
                findNavController().navigate(R.id.action_addressFragment_to_addAddressFragment)
            },
            onEditClick = { address ->
                viewModel.setSelectedAddress(address)
                findNavController().navigate(R.id.action_addressFragment_to_addAddressFragment)
            },
            onDeleteClick = { address ->
                viewModel.deleteAddress(address) { /* no-op */ }
            }
        )

        binding.rvAddress.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAddress.adapter = adapter

        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModel.loadAddress(userId)

        viewModel.addressList.observe(viewLifecycleOwner) {
            adapter.updateDataAddress(it)
            binding.tvNoAddress.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}