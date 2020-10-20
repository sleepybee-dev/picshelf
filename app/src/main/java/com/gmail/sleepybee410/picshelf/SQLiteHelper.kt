package com.gmail.sleepybee410.picshelf

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri

class SQLiteHelper(context : Context) : SQLiteOpenHelper(context, "picshelfdb", null, 2) {

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("CREATE TABLE IF NOT EXISTS PICS_TB (" +
                "idx INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "widgetId VARCHAR(100), " +
                "originUri VARCHAR(100), " +
                "uri VARCHAR(100), " +
                "label VARCHAR(20), " +
                "color VARCHAR(20));")

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS PICS_TB")
        onCreate(db)
    }

}