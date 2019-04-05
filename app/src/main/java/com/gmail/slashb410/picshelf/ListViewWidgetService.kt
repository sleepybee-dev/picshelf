package com.gmail.slashb410.picshelf

import android.content.Context
import android.content.Intent
import android.widget.RemoteViewsService
import android.widget.RemoteViews
import android.text.method.TextKeyListener.clear
import android.os.Bundle
import android.util.Log


class ListViewWidgetService  : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return ListViewRemoteViewsFactory(this.applicationContext, intent!!)
    }

}


internal class ListViewRemoteViewsFactory(private val mContext: Context, intent: Intent) :
    RemoteViewsService.RemoteViewsFactory {

    private var items: ArrayList<ListItem>? = null

    override fun onCreate() {

        items = ArrayList()

    }

    override fun getViewAt(position: Int): RemoteViews {

        val rv = RemoteViews(mContext.packageName, R.layout.item_list)

        val data = items!![position]

        rv.setTextViewText(R.id.tv_label_item, data.label)
        rv.setImageViewUri(R.id.iv_item, data.uri)
        //color

        val extras = Bundle()

//        extras.putInt(PicShelfAppWidget.EXTRA_ITEM, position)

        val fillInIntent = Intent()

//        fillInIntent.putExtra("homescreen_meeting", data)

        fillInIntent.putExtras(extras)

//        rv.setOnClickFillInIntent(R.id.lv_widget, fillInIntent)


        return rv

    }

    override fun getCount(): Int {

        Log.e("size=", items!!.size.toString())

        return items!!.size

    }

    override fun onDataSetChanged() {

        // Fetching JSON data from server and add them to items arraylist


    }

    override fun getViewTypeCount(): Int {

        return 1

    }

    override fun getItemId(position: Int): Long {

        return position.toLong()

    }

    override fun onDestroy() {

        items!!.clear()

    }

    override fun hasStableIds(): Boolean {

        return true

    }

    override fun getLoadingView(): RemoteViews? {

        return null

    }

}