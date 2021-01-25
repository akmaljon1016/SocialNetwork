package com.example.socialnetwork.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.example.socialnetwork.R
import com.example.socialnetwork.model.OnBoardingData

class TabAdapter(var context: Context, var onBoardingDataList: List<OnBoardingData>) :

    PagerAdapter() {
    override fun getCount(): Int {
        return onBoardingDataList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view: View = LayoutInflater.from(context).inflate(R.layout.layout, null)
        var imageView: ImageView = view.findViewById(R.id.splash_imageview)
        var title: TextView = view.findViewById(R.id.splash_title)
        var desc: TextView = view.findViewById(R.id.splash_desc)
        onBoardingDataList[position].imageUrl?.let { imageView.setImageResource(it) }
        title.text = onBoardingDataList[position].title
        desc.text = onBoardingDataList[position].desc
        container.addView(view)
        return view
    }
}