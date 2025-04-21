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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
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

    private var revenueMap: Map<String, Double> = emptyMap()
    private var orderCountMap: Map<String, Int> = emptyMap()
    private var avgOrderValueMap: Map<String, Double> = emptyMap()

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

        binding.spinnerTimeframe.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            Timeframe.values().map { it.name }
        )
        binding.spinnerTimeframe.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val tf = Timeframe.values()[position]
                viewModel.loadRevenue(tf)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
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

        viewModel.revenueByTime.observe(viewLifecycleOwner) { map ->
            revenueMap = map
            drawChart()
        }

        viewModel.orderCountByTime.observe(viewLifecycleOwner) { cnt ->
            orderCountMap = cnt
            updateMetricsText()
        }
        viewModel.avgOrderValueByTime.observe(viewLifecycleOwner) { aov ->
            avgOrderValueMap = aov
            updateMetricsText()
        }
    }

    private fun drawChart() {
        val keys = revenueMap.keys.toList()
        val entries = keys.mapIndexed { i, k ->
            Entry(i.toFloat(), revenueMap[k]?.toFloat() ?: 0f)
        }
        val set = LineDataSet(entries, "Revenue").apply {
            color = Color.BLACK
            setDrawCircles(true)
            lineWidth = 2f
            mode = LineDataSet.Mode.CUBIC_BEZIER
            cubicIntensity = 0.2f
            setDrawValues(false)

            setDrawFilled(true)
            fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.chart_gradient_fill)
            fillAlpha = 200
        }

        binding.lineChart.apply {
            data = LineData(set)
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                valueFormatter = IndexAxisValueFormatter(keys)
            }
            axisRight.isEnabled = false
            description.isEnabled = false
            animateX(400)
            invalidate()
        }
    }

    private fun updateMetricsText() {
        val totalOrders = orderCountMap.values.sum()
        val totalRevenue = revenueMap.values.sum()
        val avgOrderValue = if (totalOrders > 0) totalRevenue / totalOrders else 0.0

        binding.tvTotalOrders.text = "Orders: $totalOrders"
        binding.tvAvgOrderValue.text = "AOV: ${"%,.2f".format(avgOrderValue)}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}