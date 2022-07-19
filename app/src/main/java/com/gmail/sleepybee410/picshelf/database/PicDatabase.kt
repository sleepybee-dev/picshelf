package com.gmail.sleepybee410.picshelf.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Created by leeseulbee on 2022/07/12.
 */
abstract class PicDatabase : RoomDatabase() {
    abstract fun picDao() : PicDAO

    companion object {
        @Volatile private var instance: PicDatabase? = null

        fun getInstance(context: Context): PicDatabase {
            if (instance == null) {
                synchronized(PicDatabase::class.java) {
                    instance = Room.databaseBuilder(context.applicationContext,
                    PicDatabase::class.java, "picDB")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return instance!!
        }

        private fun buildDatabase(context: Context) {
            Room.databaseBuilder(context.applicationContext, PicDatabase::class.java, "picshelf.db").build()
        }
    }
}