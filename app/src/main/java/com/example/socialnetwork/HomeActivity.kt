package com.example.socialnetwork

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.socialnetwork.adapter.PagerAdapter
import com.example.socialnetwork.adapter.mainRecAdapter
import com.example.socialnetwork.databinding.ActivityHomeBinding
import com.example.socialnetwork.model.MainRecObject
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    lateinit var mAuth: FirebaseAuth
    var currentUserId: String? = null
    lateinit var adapter: mainRecAdapter
    lateinit var list: ArrayList<MainRecObject>
    lateinit var RootRef: DatabaseReference

    lateinit var viewpager: ViewPager2
    lateinit var tabLayout: TabLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        list = arrayListOf()
        RootRef = FirebaseDatabase.getInstance().getReference()
        setSupportActionBar(binding.homeToolbar)
        mAuth = FirebaseAuth.getInstance()
        currentUserId = mAuth.currentUser?.uid
        viewpager = findViewById(R.id.viewpager)
        viewpager.offscreenPageLimit = 4
        tabLayout = findViewById(R.id.tabLayout)
        val tabAdapter = PagerAdapter(this)
        binding.viewpager.adapter = tabAdapter
        TabLayoutMediator(tabLayout, viewpager) { tab, position ->
            when (position) {
                0 -> tab.setText("Chat")
                1 -> tab.setText("Groups")
                2 -> tab.setText("Contacts")
                3 -> tab.setText("Requests")
            }
        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.settings_menu -> {
                Toast.makeText(this, currentUserId, Toast.LENGTH_SHORT).show()
            }
            R.id.profile_menu -> {
                intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.findFreiends_menu -> {
                intent = Intent(this, FindFriendsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
//var a:Int=1
//    override fun onBackPressed() {
//        super.onBackPressed()
//        if (a==1){
//            Toast.makeText(this, "chiqish uchun yana 1 marta bosing", Toast.LENGTH_SHORT).show()
//          a++;
//        }
//        else{
//            finish()
//        }
//    }
//

    @RequiresApi(Build.VERSION_CODES.N)
    fun updateUserStatus(state: String) {
        val saveCurrentTime: String
        val saveCurrentDate: String
        val calendar: Calendar = Calendar.getInstance()
        val currentDate: SimpleDateFormat = SimpleDateFormat("MMM dd, yyyy")
        saveCurrentDate = currentDate.format(calendar.time)
        val currentTime: SimpleDateFormat = SimpleDateFormat("hh: mm a")
        saveCurrentTime = currentTime.format(calendar.time)
        val onlineStateMap: HashMap<String, String> = HashMap()
        onlineStateMap.put("time", saveCurrentTime)
        onlineStateMap.put("date", saveCurrentDate)
        onlineStateMap.put("state", state)
        RootRef.child("Users").child(currentUserId.toString()).child("userState")
            .updateChildren(onlineStateMap as Map<String, Any>)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()
        updateUserStatus("online")
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStop() {
        super.onStop()
        updateUserStatus("offline")
    }
}