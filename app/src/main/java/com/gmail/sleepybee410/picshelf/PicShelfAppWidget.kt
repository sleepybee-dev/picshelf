package com.gmail.sleepybee410.picshelf

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.FileProvider
import android.util.Log
import android.widget.RemoteViews
import com.amitshekhar.BuildConfig
import com.gmail.sleepybee410.picshelf.Activity.MainActivity
import java.io.File
import java.net.URI


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [PicShelfAppWidgetConfigureActivity]
 */
class PicShelfAppWidget : AppWidgetProvider() {

    var context : Context? = null

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)

    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        var manager = AppWidgetManager.getInstance(context)
//        initUI(context, manager, manager.getAppWidgetIds(object : ComponentName(context, getclass)))

    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
//        for (appWidgetId in appWidgetIds) {
//            PicShelfAppWidgetConfigureActivity.deleteTitlePref(context, appWidgetId)
//        }
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
            val dbList = loadData(context);
            val file = File(URI(dbList.last().uri.toString()))
            Log.d("SB", dbList.last().uri.toString());
            val uri = FileProvider.getUriForFile(context, "com.gmail.sleepybee410.picshelf.fileProvider", file)
            context.grantUriPermission( "com.sec.android.app.launcher", uri, Intent.FLAG_GRANT_READ_URI_PERMISSION );
            views.setImageViewUri(R.id.iv_widget, uri)

            appWidgetManager.updateAppWidget(appWidgetId, views)
//            views.setRemoteAdapter(appWidgetId, R.id.lv_widget, intent)

            val startActivityIntent = Intent(context, MainActivity::class.java)
            val startActivityPendingIntent =
                PendingIntent.getActivity(
                    context,
                    0,
                    startActivityIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
//            val uri = Uri.parse(
//                dbList.last().originUri
//            )
            views.setPendingIntentTemplate(R.id.iv_widget, startActivityPendingIntent)
//            views.setEmptyView(R.id.lv_widget, R.i)
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
                        var idx : Int = cursor.getInt(cursor.getColumnIndex("idx"))
                        var label = cursor.getString(cursor.getColumnIndex("label"))
                        var color = cursor.getString(cursor.getColumnIndex("color"))
                        var originUri = cursor.getString(cursor.getColumnIndex("originUri"))
                        var uri = cursor.getString(cursor.getColumnIndex("uri"))

                        var item = ListItem(idx, Uri.parse(originUri), Uri.parse(uri), label, color)
                        items.add(item)

                    } while (cursor.moveToNext())
                }
            }

            db.close()

            print("dblist : "+items.size);
            return items

        }
    }

}

