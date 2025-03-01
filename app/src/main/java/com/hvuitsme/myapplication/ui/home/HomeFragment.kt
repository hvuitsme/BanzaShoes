package com.hvuitsme.myapplication.ui.home

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat

import com.hvuitsme.myapplication.R
import com.hvuitsme.myapplication.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val handler = Handler(Looper.getMainLooper())

    companion object {
        fun newInstance() = HomeFragment()
    }

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        return inflater.inflate(R.layout.fragment_home, container, false)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeSwipeRefresh.setOnRefreshListener {
            binding.homeLoading.visibility = View.VISIBLE
            binding.loadingBg.visibility = View.VISIBLE

            binding.homeSwipeRefresh.isRefreshing = false
//            Toast.makeText(requireContext(), "Đang làm mới.....", Toast.LENGTH_SHORT).show()

            handler.postDelayed({
                binding.homeLoading.visibility = View.GONE
                binding.loadingBg.visibility = View.GONE
            }, 2000)
        }

        binding.topAppBar.setNavigationOnClickListener {
            binding.drawlayoutFragment.openDrawer(GravityCompat.START)
        }
    }
}