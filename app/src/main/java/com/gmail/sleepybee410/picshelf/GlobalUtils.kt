package com.gmail.sleepybee410.picshelf

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File


object GlobalUtils {

    fun deleteDataByWidgetId(context: Context, widgetId: Int) {
        val helper = SQLiteHelper(context)
        val db = helper.writableDatabase
        val picItem = loadByWidgetId(context, widgetId)
        if (picItem != null) {
            db.execSQL("DELETE FROM PICS_TB WHERE widgetId=$widgetId")
            if (picItem.uri != null) {
                deleteFile(picItem.uri!!)
            }
        }
    }

    private fun deleteFile(path: Uri) {
        val file = File(path.path)
        if(file.exists()) {
            if(file.delete()) {
                Log.d("SB", "===============file deleted")
            }
        } else {

            Log.d("SB", "===============file not exists")
        }
    }

    fun deleteItem(context: Context, originUri:Uri){
        val helper = SQLiteHelper(context)
        val db = helper.writableDatabase
        db.execSQL("DELETE FROM PICS_TB WHERE originUri='$originUri'")

    }

    var ITEM_WIDTH : Int = 0
    var ITEM_HEIGHT : Int = 0

    fun loadByWidgetId(context: Context, widgetId: Int) : PicItem? {
        Log.d("SB", "loadByWidgetId : $widgetId")
        val helper = SQLiteHelper(context)
        val db = helper.writableDatabase
        val result = db.rawQuery("SELECT * FROM PICS_TB WHERE widgetId= $widgetId", null)
        return if(result.moveToFirst()) {
            PicItem(
                result.getInt(0),
                result.getString(1),
                result.getInt(2),
                Uri.parse(result.getString(3)),
                Uri.parse(result.getString(4)),
                result.getString(5),
                result.getString(6),
                result.getString(7)
            )
        } else null
    }

    fun loadAll(context: Context) : ArrayList<PicItem> {
        var resultList : ArrayList<PicItem>  = arrayListOf()
        val helper = SQLiteHelper(context)
        val db = helper.writableDatabase
        val result = db.rawQuery("SELECT * FROM PICS_TB", null)
        while(result.moveToNext()) {
            resultList.add(PicItem(
                result.getInt(0),
                result.getString(1),
                result.getInt(2),
                Uri.parse(result.getString(3)),
                Uri.parse(result.getString(4)),
                result.getString(5),
                result.getString(6),
                result.getString(7)
            ))
        }
        return resultList
    }
}