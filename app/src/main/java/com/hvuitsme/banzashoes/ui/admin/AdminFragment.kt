package com.hvuitsme.banzashoes.ui.admin

import android.graphics.Color
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.hvuitsme.banzashoes.databinding.FragmentAdminBinding

class AdminFragment : Fragment() {
    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = AdminFragment()
    }

    private val viewModel: AdminViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        return inflater.inflate(R.layout.fragment_admin, container, false)
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLineChart()
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