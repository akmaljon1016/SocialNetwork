package com.example.socialnetwork.model

data class Messages(
    val from: String,
    val message: String,
    val type: String,
    val to:String,
    val messageId:String,
    val time:String,
    val date:String
)
{
    constructor():this("","","","","","","")
}
