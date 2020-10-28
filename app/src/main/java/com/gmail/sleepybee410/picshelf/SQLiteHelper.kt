package com.gmail.sleepybee410.picshelf

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri

class SQLiteHelper(context : Context) : SQLiteOpenHelper(context, "picshelfdb", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("CREATE TABLE IF NOT EXISTS PICS_TB (" +
                "idx INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "createDate VARCHAR(100), " +
                "widgetId INTEGER, " +
                "originUri VARCHAR(100), " +
                "uri VARCHAR(100), " +
                "label VARCHAR(20), " +
                "color VARCHAR(20), " +
                "frame VARCHAR(20));")


    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS PICS_TB")
        onCreate(db)
    }

}