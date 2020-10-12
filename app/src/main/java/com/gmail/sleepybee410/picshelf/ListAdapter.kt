package com.gmail.sleepybee410.picshelf

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.gmail.sleepybee410.picshelf.Activity.EditActivity
import com.gmail.sleepybee410.picshelf.Activity.MainActivity

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
        val into = Glide.with(context)
            .load(list!![position].uri)
            .into(holder.picView)
        holder.itemView.setOnLongClickListener {
            var builder : AlertDialog.Builder = AlertDialog.Builder(context)
            var options : Array<String> = arrayOf("수정", "삭제")
//            builder.
            builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
                when(which){
                    0-> edit(list[position])
                    1->{
                        GlobalUtils.deleteItem(context, list[position].originUri)
                        list.remove(list[position])
                    }
                    else->Toast.makeText(context, options[which], Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()

            })
            builder.setOnDismissListener {
                notifyDataSetChanged()
            }
            var dialog : AlertDialog = builder.create()
            dialog.show()

            true
        }




    }

    private fun edit(item: ListItem) {

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
        var picView  = view.findViewById(R.id.iv_item) as ImageView

    }


}