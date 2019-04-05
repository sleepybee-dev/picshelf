package com.gmail.slashb410.picshelf

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ListView
import android.widget.RemoteViews
import com.gmail.slashb410.picshelf.R
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.pic_shelf_app_widget.view.*
import com.gmail.slashb410.picshelf.Activity.MainActivity
import android.app.PendingIntent
import android.content.ComponentName


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

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        var manager = AppWidgetManager.getInstance(context)
        initUI(context, manager, manager.getAppWidgetIds(object : ComponentName(context, getclass)))

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

//            val widgetText = PicShelfAppWidgetConfigureActivity.loadTitlePref(context, appWidgetId)
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.pic_shelf_app_widget)

            var intent = Intent(context, ListViewWidgetService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
            views.setRemoteAdapter(appWidgetId, R.id.lv_widget, intent)

            val startActivityIntent = Intent(context, MainActivity::class.java)
            val startActivityPendingIntent =
                PendingIntent.getActivity(context, 0, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setPendingIntentTemplate(R.id.lv_widget, startActivityPendingIntent)
//            views.setEmptyView(R.id.lv_widget, R.i)
        }
    }


    private fun loadData(context: Context): ArrayList<ListItem> {

        val helper = SQLiteHelper(context)
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

