package com.gmail.slashb410.picshelf

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide

class ListAdapter (list : ArrayList<ListItem>) : RecyclerView.Adapter<ListAdapter.ViewHolder>(), View.OnClickListener {

    override fun onClick(v: View?) {
        var position = v!!.tag
        Toast.makeText(v.context, "postion : $position", Toast.LENGTH_SHORT).show()

    }

    private var list = list
    private lateinit var context : Context

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        this.context = p0.context
        var view = LayoutInflater.from(p0!!.context).inflate(R.layout.item_list, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.label.text = list!![position].label
        Glide.with(context)
            .load(list!![position].uri)
            .into(holder.picView)
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view){
        var label = view.findViewById(R.id.tv_label_item) as TextView
        var picView  = view.findViewById(R.id.iv_item) as ImageView
    }
}