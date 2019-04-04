package com.gmail.slashb410.picshelf

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Parcel
import android.os.Parcelable

class SQLiteHelper(context : Context) : SQLiteOpenHelper(context, "picshelfdb", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("CREATE TABLE IF NOT EXISTS PICS_TB (" +
                "idx INTEGER PRIMARY KEY NOT NULL, " +
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