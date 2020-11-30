package com.gmail.sleepybee410.picshelf.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gmail.sleepybee410.picshelf.activity.EditActivity
import com.gmail.sleepybee410.picshelf.activity.MainActivity
import com.gmail.sleepybee410.picshelf.GlobalUtils
import com.gmail.sleepybee410.picshelf.PicItem
import com.gmail.sleepybee410.picshelf.R

class PicListAdapter (pic : ArrayList<PicItem>) : RecyclerView.Adapter<PicListAdapter.ViewHolder>(), View.OnClickListener {

    override fun onClick(v: View?) {
        var position = v!!.tag
        Toast.makeText(v.context, "postion : $position", Toast.LENGTH_SHORT).show()

    }

    private var list = pic
    private lateinit var context : Context

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        this.context = p0.context
        var view = LayoutInflater.from(p0!!.context).inflate(R.layout.item_list, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.label.text = list[position].label
        holder.date.text = list[position].createDate.substring(0, 10)
        val into = Glide.with(context)
            .load(list[position].uri)
            .into(holder.picView)
//        holder.btnDelete.setOnClickListener {
//            var builder : AlertDialog.Builder = AlertDialog.Builder(context)
//            var options : Array<String> = arrayOf("삭제")
//
//            builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
//                when(which){
//                    0->{
//                        GlobalUtils.deleteItem(context, list[position].originUri)
//                        list.remove(list[position])
//                    }
//                    else->Toast.makeText(context, options[which], Toast.LENGTH_SHORT).show()
//                }
//                dialog.dismiss()
//
//            })
//            builder.setOnDismissListener {
//                notifyDataSetChanged()
//            }
//            var dialog : AlertDialog = builder.create()
//            dialog.show()
//
//            true
//        }

    }

    private fun edit(item: PicItem) {

        var intent = Intent(context, EditActivity::class.java)
        intent.putExtra("idx", item.idx)
        intent.putExtra("originUri", item.originUri)
        intent.putExtra("uri", item.uri)
        intent.putExtra("label", item.label)
        intent.putExtra("color", item.color)
        (context as MainActivity).startActivityForResult(intent, MainActivity.REQUEST_SELECT)

    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view){
        var label = view.findViewById(R.id.tv_label_item) as TextView
        var date = view.findViewById(R.id.tv_date_item) as TextView
        var picView  = view.findViewById(R.id.iv_item) as ImageView
//        var btnDelete = view.findViewById(R.id.btn_delete_item) as ImageButton

    }


}