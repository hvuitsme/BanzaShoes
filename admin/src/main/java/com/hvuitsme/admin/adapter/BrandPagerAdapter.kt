package com.hvuitsme.admin.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hvuitsme.admin.ui.product.BrandFragment

class BrandPagerAdapter(
    fragment: Fragment,
    private val brands: List<String>
): FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return BrandFragment.newInstance(brands[position])
    }

    override fun getItemCount(): Int = brands.size
}