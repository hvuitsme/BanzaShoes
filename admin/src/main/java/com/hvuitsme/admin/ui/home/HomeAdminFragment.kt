package com.hvuitsme.admin.ui.home

import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.hvuitsme.admin.R
import com.hvuitsme.admin.adapter.RecentOrderAdapter
import com.hvuitsme.admin.data.remote.OrderDataSource
import com.hvuitsme.admin.data.repository.OrderRepoImpl
import com.hvuitsme.admin.databinding.FragmentHomeAdminBinding
import kotlin.getValue

class HomeAdminFragment : Fragment() {
    private var _binding: FragmentHomeAdminBinding? = null
    private val binding get() = _binding!!

    private lateinit var recentAdapter: RecentOrderAdapter

    companion object {
        fun newInstance() = HomeAdminFragment()
    }

    private lateinit var viewModel: HomeAdminViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = OrderRepoImpl(OrderDataSource())
        val factory = HomeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[HomeAdminViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        return inflater.inflate(R.layout.fragment_admin, container, false)
        _binding = FragmentHomeAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLineChart()

        binding.adminToolbar.setNavigationOnClickListener {
            val intent = Intent().apply {
                component = ComponentName(
                    "com.hvuitsme.banzashoes",
                    "com.hvuitsme.banzashoes.MainActivity"
                )
            }
            requireContext().startActivity(intent)
            requireActivity().finish()
        }

        val navOption = navOptions {
            anim {
                enter    = R.anim.slide_in_from_right
                exit     = R.anim.slide_out_to_left
                popEnter = R.anim.pop_slide_in_from_left
                popExit  = R.anim.pop_slide_out_from_right
            }
        }

        binding.llBanner.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeAdminFragment_to_bannerFragment,
                null,
                navOption
            )
        }

        binding.llProduct.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeAdminFragment_to_productFragment,
                null,
                navOption
            )
        }

        binding.llCategory.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeAdminFragment_to_categoryFragment,
                null,
                navOption
            )
        }

        binding.llOrder.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeAdminFragment_to_orderFragment,
                null,
                navOption)
        }

        recentAdapter = RecentOrderAdapter(emptyList()) { order ->
            findNavController().navigate(
                R.id.action_homeAdminFragment_to_orderDetailFragment,
                bundleOf("orderId" to order.id),
                navOption
            )
        }
        binding.rvRecent.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecent.adapter = recentAdapter

        viewModel.orders.observe(viewLifecycleOwner) {
            recentAdapter.updateData(it)
        }
    }

    private fun setupLineChart() {
        val entries = mutableListOf<Entry>()
        entries.add(Entry(6f, 112f))
        entries.add(Entry(8f, 115f))
        entries.add(Entry(10f, 114f))
        entries.add(Entry(12f, 110f))
        entries.add(Entry(14f, 116f))
        entries.add(Entry(16f, 113f))
        entries.add(Entry(18f, 115f))
        entries.add(Entry(20f, 112f))

        val lineDataSet = LineDataSet(entries, "Ví dụ DataSet")
        lineDataSet.color = Color.BLACK
        lineDataSet.setDrawValues(false)
        lineDataSet.setDrawCircles(true)
        lineDataSet.circleRadius = 4f
        lineDataSet.circleHoleRadius = 2f
        lineDataSet.setCircleColor(Color.BLACK)
        lineDataSet.lineWidth = 2f

        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineDataSet.cubicIntensity = 0.2f

        val data = LineData(lineDataSet)

        binding.lineChart.data = data

        binding.lineChart.description.isEnabled = false
        binding.lineChart.setPinchZoom(true)
        binding.lineChart.xAxis.setDrawGridLines(false)
        binding.lineChart.axisLeft.setDrawGridLines(true)
        binding.lineChart.axisRight.isEnabled = false

        val xAxis = binding.lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 2f
        xAxis.axisMinimum = 6f
        xAxis.axisMaximum = 20f

        val leftAxis = binding.lineChart.axisLeft
        leftAxis.axisMinimum = 100f
        leftAxis.axisMaximum = 120f

        binding.lineChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}