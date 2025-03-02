package com.hvuitsme.myapplication.ui.home

import android.content.res.Configuration
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

import com.hvuitsme.myapplication.R
import com.hvuitsme.myapplication.databinding.FragmentHomeBinding
import com.hvuitsme.myapplication.ui.cart.CartFragment

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
            val drawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.drawlayout_main)
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val logoRes = if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            R.drawable.logo_banza_invert
        } else {
            R.drawable.logo_banza
        }

        binding.topAppBar.findViewById<ImageView>(R.id.logo_home)?.setImageResource(logoRes)

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId){
                R.id.cart_toolbar -> {
                    parentFragmentManager
                        .beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_in_from_right,
                            R.anim.slide_out_to_left,
                            R.anim.pop_slide_in_from_left,
                            R.anim.pop_slide_out_from_right
                        )
                        .replace(binding.container.id, CartFragment.newInstance())
                        .addToBackStack(null)
                        .commit()
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}