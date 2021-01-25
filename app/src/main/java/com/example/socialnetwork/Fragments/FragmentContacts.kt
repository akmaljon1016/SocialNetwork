package com.example.socialnetwork.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialnetwork.R
import com.example.socialnetwork.adapter.mainRecAdapter
import com.example.socialnetwork.databinding.FragmentContactsBinding
import com.example.socialnetwork.model.MainRecObject
import com.example.socialnetwork.viewmodels.FragmentContactViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FragmentContacts : Fragment() {
    lateinit var binding: FragmentContactsBinding
    lateinit var viewModel: FragmentContactViewModel
    var contactsList: ArrayList<MainRecObject>? = null
    lateinit var RootRef: DatabaseReference
    lateinit var RootRefUser: DatabaseReference
    lateinit var mAuth: FirebaseAuth
    lateinit var currentUserKey: String
    lateinit var adapter: mainRecAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contacts, container, false)
        viewModel = ViewModelProvider(this).get(FragmentContactViewModel::class.java)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contactsList = arrayListOf()
        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        adapter = mainRecAdapter(requireContext())
        viewModel.contactList.observe(viewLifecycleOwner, Observer {
           adapter.implementRec(it)
        })
        binding.recyclerview.adapter = adapter
    }
//    comment
}