package com.example.socialnetwork.model

import android.widget.ImageView
import android.widget.TextView

data class MainRecObject(
    val name: String,
    val phoneNumber:String,
    val profileImage: String,
    val uid:String
//    val rec_main_last_message: String,
//    val rec_main_Clock: String
){
    constructor() : this("","","","")
}