package com.gmail.sleepybee410.picshelf.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.gmail.sleepybee410.picshelf.activity.SingleImageActivity
import com.gmail.sleepybee410.picshelf.PicItem
import com.gmail.sleepybee410.picshelf.R

/**
 * Created by leeseulbee on 2020/11/09.
 */
class HistoryGridAdapter (private var context : Context, private var list: ArrayList<PicItem>) : BaseAdapter() {

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder : ViewHolder
        var convertView = convertView
        if (convertView == null) {
            holder = ViewHolder()
            convertView = LayoutInflater.from(context).inflate(R.layout.item_grid, parent, false)
            holder.picView = convertView!!.findViewById(R.id.iv_item_grid)
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }

        Glide.with(context)
            .load(list[position].uri)
            .into(holder.picView)

        holder.picView.setOnClickListener {
            val intent = Intent(context, SingleImageActivity::class.java)
            intent.putExtra("dbIdx", list[position].idx)
            context.startActivity(intent)
        }

        return convertView
    }

    class ViewHolder {
        lateinit var picView : ImageView
    }

}