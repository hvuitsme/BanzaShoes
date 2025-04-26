package com.hvuitsme.banzashoes.ui.order

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OrderPagerAdapter(
    fa: FragmentActivity,
    private val statuses: List<String>
) : FragmentStateAdapter(fa) {
    override fun getItemCount() = statuses.size
    override fun createFragment(position: Int): Fragment {
        return OrderListFragment.newInstance(statuses[position])
    }
}