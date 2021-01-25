package com.example.socialnetwork.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialnetwork.R
import com.example.socialnetwork.model.MainRecObject
import org.w3c.dom.Text

class mainRecAdapter(val context: Context) :
    RecyclerView.Adapter<mainRecAdapter.mainRecViewHolder>() {

    var listener: onItemClick? = null
    private var mainList = arrayListOf<MainRecObject>()

    class mainRecViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rec_main_image = view.findViewById<ImageView>(R.id.rec_item_image)
        val rec_main_username = view.findViewById<TextView>(R.id.rec_item_userName)
        val rec_main_phoneNumber = view.findViewById<TextView>(R.id.rec_item_phoneNumber)
        val rec_main_clock = view.findViewById<TextView>(R.id.rec_item_clock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mainRecViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.recyclerview_item, parent, false)
        return mainRecViewHolder(view)
    }

    fun implementRec(mainList: ArrayList<MainRecObject>) {
        this.mainList = mainList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: mainRecViewHolder, position: Int) {
        val currentObject = mainList.get(position)
        //holder.rec_main_image.setImageResource(currentObject.rec_main_Image)
        Glide.with(context).load(currentObject.profileImage)
            .placeholder(R.drawable.person)
            .into(holder.rec_main_image)
        holder.rec_main_username.setText(currentObject.name)
        holder.rec_main_phoneNumber.setText(currentObject.phoneNumber)
        holder.itemView.setOnClickListener {
            listener?.userClick(currentObject)
        }
//        holder.rec_main_clock.setText(currentObject.rec_main_Clock)
    }

    override fun getItemCount(): Int {
        return mainList.size
    }

    interface onItemClick {
        fun userClick(user: MainRecObject)
    }

    fun setOnItemClickListener(onItemClickListener: onItemClick) {
        this.listener = onItemClickListener
    }
}