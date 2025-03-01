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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

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

        val headerView = binding.navView.getHeaderView(0)
        val avatarImageView = headerView.findViewById<ImageView>(R.id.avatar)

        val radiusPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            20f,
            resources.displayMetrics
        ).toInt()

        Glide.with(requireContext())
            .load(R.drawable.avatar1)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(radiusPx)))
            .into(avatarImageView)

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val logoRes = if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            R.drawable.logo_banza_invert
        } else {
            R.drawable.logo_banza
        }

        binding.topAppBar.findViewById<ImageView>(R.id.logo_home)?.setImageResource(logoRes)
    }
}