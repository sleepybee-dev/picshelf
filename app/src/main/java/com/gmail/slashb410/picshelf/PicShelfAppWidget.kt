package com.gmail.slashb410.picshelf

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.net.Uri
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.RemoteViews
import com.gmail.slashb410.picshelf.R
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.pic_shelf_app_widget.view.*

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [PicShelfAppWidgetConfigureActivity]
 */
class PicShelfAppWidget : AppWidgetProvider() {

    var context : Context? = null

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            PicShelfAppWidgetConfigureActivity.deleteTitlePref(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        internal fun updateAppWidget(
            context: Context, appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {

            val widgetText = PicShelfAppWidgetConfigureActivity.loadTitlePref(context, appWidgetId)
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.pic_shelf_app_widget)
            views.setTextViewText(R.id.appwidget_text, widgetText)

            var list = loadData()
            var adapter : ListAdapter = ListAdapter(list)

            var mLayoutManager = LinearLayoutManager(this)
            views.getlaylayoutManager = mLayoutManager
            views.setRemoteAdapter(R.id.lv_widget, adapter)


            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }


    private fun loadData(): ArrayList<ListItem> {

        val helper = SQLiteHelper(this)
        val db = helper.writableDatabase

//        var db = this.openOrCreateDatabase("picshelf", Context.MODE_PRIVATE, null)
//        db.execSQL("CREATE TABLE IF NOT EXISTS PICS_TB;")

        var cursor = db.rawQuery("SELECT * FROM PICS_TB", null)
        var items : ArrayList<ListItem> = ArrayList()

        if(cursor!=null){
            if(cursor.moveToFirst()){
                do{
                    var label = cursor.getString(cursor.getColumnIndex("label"))
                    var color = cursor.getString(cursor.getColumnIndex("color"))
                    var originUri = cursor.getString(cursor.getColumnIndex("originUri"))
                    var uri = cursor.getString(cursor.getColumnIndex("uri"))

                    var item = ListItem(Uri.parse(originUri), Uri.parse(uri), label, color)
                    items.add(item)

                } while (cursor.moveToNext())
            }
        }

        db.close()

        return items

    }
}

