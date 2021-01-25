package com.example.socialnetwork.adapter

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.socialnetwork.Fragments.FragmentChat
import com.example.socialnetwork.Fragments.FragmentContacts
import com.example.socialnetwork.Fragments.FragmentGroup
import com.example.socialnetwork.Fragments.FragmentRequest

class PagerAdapter(activity:AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return FragmentChat()
            1 -> return FragmentGroup()
            2 -> return FragmentContacts()
            3 -> return FragmentRequest()
            else -> return FragmentChat()
        }
    }
}