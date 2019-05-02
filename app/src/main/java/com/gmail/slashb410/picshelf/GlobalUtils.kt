package com.gmail.slashb410.picshelf

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
}