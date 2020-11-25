package com.gmail.sleepybee410.picshelf

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.FileProvider
import com.gmail.sleepybee410.picshelf.activity.EditActivity
import java.io.File
import java.lang.String
import java.net.URI


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [PicShelfAppWidgetConfigureActivity]
 */
class WidgetProvider : AppWidgetProvider() {

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
        if (context != null && appWidgetManager != null) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val intentAction = intent!!.action
        if (intentAction == ACTION_CLICK) {
            Log.d("SB", "=========ACTION_CLICK")
//            updateWidgetState(paramContext, str)
            val extras = intent!!.extras
            val appWidgetId = extras!!.getInt("widgetId")
            val editIntent = Intent(context, EditActivity::class.java)
            editIntent.putExtra("widgetId", appWidgetId)

            editIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context!!.startActivity(editIntent)
//            intent.
//            val objNum = myPrefs.getInt(PREF_PREFIX_KEY + appWidgetId, -1)
//            if (objNum > -1) {
//                val pa: PageAction = EditActivity
//                    .GetPageObjectAtIndex(context, widgetPageName, objNum) as PageAction
//                pa.ExecuteActionFromWidgetClick(context)
//            }
        } else {
            super.onReceive(context, intent)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            GlobalUtils.deleteDataByWidgetId(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        val ACTION_CLICK = "ACTION_CLICK_WIDGET"

        internal fun updateAppWidget(
            context: Context, appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {

//            val widgetText = PicShelfAppWidgetConfigureActivity.loadTitlePref(context, appWidgetId)
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.pic_shelf_app_widget)

//            var intent = Intent(context, ListViewWidgetService::class.java)
//            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
//            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
            // Instruct the widget manager to update the widget

//            val dbIdx = extras.getInt("dbIdx")
            val loadedItem = GlobalUtils.loadByWidgetId(context, appWidgetId) ?: return
            val file = File(URI(loadedItem.uri.toString()))
            val uri = FileProvider.getUriForFile(
                context,
                "com.gmail.sleepybee410.picshelf.fileProvider",
                file
            )

            context.grantUriPermission(
                "com.sec.android.app.launcher",
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            );
            views.setImageViewUri(R.id.iv_widget, uri)
            when (loadedItem.frame) {
                "polaroid" -> {
                    views.setViewPadding(R.id.fl_frame_widget, 16, 16, 16, 80)
                    views.setTextViewText(R.id.tv_label_widget, loadedItem.label)
                    views.setViewVisibility(R.id.tv_label_widget, View.VISIBLE)
                }
                "default" -> {
                    views.setViewPadding(R.id.fl_frame_widget, 16, 16, 16, 16)
                    views.setViewVisibility(R.id.tv_label_widget, View.GONE)
                }
                else -> {
                    views.setViewPadding(R.id.fl_frame_widget, 0, 0, 0, 0)
                    views.setViewVisibility(R.id.tv_label_widget, View.GONE)
                }
            }

            val startActivityIntent = Intent(context, WidgetProvider::class.java)
            startActivityIntent.action = ACTION_CLICK
            startActivityIntent.putExtra("widgetId", appWidgetId)

            val data = Uri.withAppendedPath(
                Uri.parse("picShelf://widget/id/"), String.valueOf(appWidgetId)
            )
            startActivityIntent.data = data
            val startActivityPendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    0,
                    startActivityIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

            views.setOnClickPendingIntent(R.id.fl_frame_widget, startActivityPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

}

