package com.gmail.sleepybee410.picshelf

import android.content.Context
import android.net.Uri


object GlobalUtils {

    fun deleteItem(context: Context, originUri:Uri){
        val helper = SQLiteHelper(context)
        val db = helper.writableDatabase
        db.execSQL("DELETE FROM PICS_TB WHERE originUri='$originUri'")

    }

    var ITEM_WIDTH : Int = 0
    var ITEM_HEIGHT : Int = 0

    fun loadByDBIdx(context: Context, dbIdx: Int) : PicItem? {
        val helper = SQLiteHelper(context)
        val db = helper.writableDatabase
        val result = db.rawQuery("SELECT * FROM PICS_TB WHERE idx='$dbIdx'", null)
        if(result.moveToFirst()) {
            return PicItem(
                result.getInt(0),
                result.getString(1),
                result.getInt(2),
                Uri.parse(result.getString(3)),
                Uri.parse(result.getString(4)),
                result.getString(5),
                result.getString(6),
                result.getString(7)
            )
        }
        return null
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