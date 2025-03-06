package com.hvuitsme.banzashoes.ui.home

import android.content.res.Configuration
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hvuitsme.banzashoes.R
import com.hvuitsme.banzashoes.adapter.CarouselAdapter
import com.hvuitsme.banzashoes.data.model.Carousel
import com.hvuitsme.banzashoes.databinding.FragmentHomeBinding
import com.hvuitsme.banzashoes.ui.cart.CartFragment
import com.hvuitsme.banzashoes.ui.login.SigninFragment
import com.hvuitsme.banzashoes.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val autoScrollDelay = 4000L
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var viewPager: ViewPager2
    private lateinit var carouselAdapter: CarouselAdapter
    private lateinit var databaseReference: DatabaseReference

    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            viewPager.setCurrentItem(viewPager.currentItem + 1, true)
            handler.postDelayed(this, autoScrollDelay)
        }
    }
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
            when (menuItem.itemId) {
                R.id.cart_toolbar -> {
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    if(currentUser != null){
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
                    }else{
                        parentFragmentManager
                            .beginTransaction()
                            .setCustomAnimations(
                                R.anim.pop_slide_in_from_left,
                                R.anim.pop_slide_out_from_right,
                                R.anim.slide_in_from_right,
                                R.anim.slide_out_to_left
                            )
                            .replace(binding.container.id, SigninFragment.newInstance())
                            .addToBackStack(null)
                            .commit()
                    }
                    true
                }
                else -> false
            }
        }

        viewPager = binding.carouselViewpager2

        viewPager.setPageTransformer { page, position ->
            page.alpha = 1 - kotlin.math.abs(position)
            page.translationX = -position * page.width
            val scale = 0.85f + (1 - kotlin.math.abs(position)) * 0.15f
            page.scaleX = scale
            page.scaleY = scale
        }

        carouselAdapter = CarouselAdapter(emptyList())
        viewPager.adapter = carouselAdapter

        databaseReference = FirebaseDatabase.getInstance().getReference("Banner")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val carouselList = mutableListOf<Carousel>()
                for (child in snapshot.children) {
                    val carouselItem = child.getValue(Carousel::class.java)
                    if (carouselItem != null){
                        carouselList.add(carouselItem)

                    }
                }
                Log.d("HomeFragment", "carouselList: $carouselList")
                carouselAdapter.updateData(carouselList)

                if (carouselList.isNotEmpty()){
                    val startPosition = Int.MAX_VALUE / 2 - (Int.MAX_VALUE / 2) % carouselList.size
                    viewPager.setCurrentItem(startPosition, false)
                }
                handler.postDelayed(autoScrollRunnable, autoScrollDelay)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment", "Error fetching carousel data: ${error.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}