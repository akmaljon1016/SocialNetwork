package com.example.socialnetwork

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialnetwork.adapter.mainRecAdapter
import com.example.socialnetwork.databinding.ActivityFindFriendsBinding
import com.example.socialnetwork.model.MainRecObject
import com.example.socialnetwork.viewmodels.FindFriendsViewModel

class FindFriendsActivity : AppCompatActivity() {
    lateinit var binding: ActivityFindFriendsBinding
    lateinit var adapter: mainRecAdapter
    lateinit var viewModel: FindFriendsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_find_friends)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        viewModel = ViewModelProvider(this).get(FindFriendsViewModel::class.java)
        findFRec()
        adapter.setOnItemClickListener(object : mainRecAdapter.onItemClick {
            override fun userClick(user: MainRecObject) {
                val uid = user.uid
                val intent = Intent(this@FindFriendsActivity, SendRequestActivity::class.java)
                intent.putExtra("uid", uid)
                startActivity(intent)
            }
        })
    }

    fun findFRec() {
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = mainRecAdapter(this)
        viewModel.findFriendList.observe(this, Observer {
            adapter.implementRec(it)
        })
//        adapter.implementRec(list)
        binding.recyclerView.adapter = adapter
    }
}