package com.example.socialnetwork

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.viewpager.widget.ViewPager
import com.example.socialnetwork.adapter.TabAdapter
import com.example.socialnetwork.model.OnBoardingData
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    var OnBoardingViewPagerAdapter: TabAdapter? = null
    lateinit var tabLayout: TabLayout
    var onBoardingViewPager: ViewPager? = null
    lateinit var btn_skip: Button
    var position: Int? = null
    var sharedPreferences: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (restorePrefData() && restorePrefDataAccount()) {
            intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        if (restorePrefData() && !restorePrefDataAccount()) {
            intent = Intent(this, CreateAccount::class.java)
            startActivity(intent)
            finish()
        }

        setContentView(R.layout.activity_main)
        btn_skip = findViewById(R.id.btn_skip)
        onBoardingViewPager = findViewById(R.id.screenPager)
        tabLayout = findViewById(R.id.tabLayout)
        val onBoardingData: MutableList<OnBoardingData> = arrayListOf()
        onBoardingData.add(OnBoardingData("Message", resources.getString(R.string.Lorem), null))
        onBoardingData.add(OnBoardingData("Message", resources.getString(R.string.Lorem), null))
        onBoardingData.add(
            OnBoardingData(
                "Message",
                resources.getString(R.string.Lorem),
                R.drawable.vectorsas
            )
        )
        setOnBoardingViewPagerAdapter(onBoardingData)
        btn_skip.setOnClickListener {
            savePrefData()
            intent = Intent(this, CreateAccount::class.java)
            startActivity(intent)
            finish()
        }
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                position = tab!!.position
                if (tab.position == onBoardingData.size - 1) {
                    btn_skip.text = "Get Started"
                } else {
                    btn_skip.text = "Skip"
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
    }

    private fun setOnBoardingViewPagerAdapter(onBoardingData: List<OnBoardingData>) {
        OnBoardingViewPagerAdapter = TabAdapter(this, onBoardingData)
        onBoardingViewPager!!.adapter = OnBoardingViewPagerAdapter
        tabLayout?.setupWithViewPager(onBoardingViewPager)
    }

    private fun savePrefData() {
        sharedPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
        editor.putBoolean("isFirstTimeRun", true)
        editor.apply()
    }

    private fun restorePrefData(): Boolean {
        sharedPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE)
        return sharedPreferences!!.getBoolean("isFirstTimeRun", false)
    }

    private fun restorePrefDataAccount(): Boolean {
        sharedPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE)
        return sharedPreferences!!.getBoolean("isCreated", false)
    }
}