package com.example.stayhealthy.ui.adapters


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.stayhealthy.ui.fragments.FoodMenuFragment
import java.util.*


class MenuSlidePagerAdapter(fragmentActivity: FragmentActivity, private val nodes: ArrayList<String>) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return nodes.size
    }

    fun getTitle(position: Int): String {
        return nodes[position]
    }

    override fun createFragment(position: Int): Fragment {
        return FoodMenuFragment.newInstance(nodes[position])
    }
}
