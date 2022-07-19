package com.gmail.sleepybee410.picshelf.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.gmail.sleepybee410.picshelf.PicItem

/**
 * Created by leeseulbee on 2022/07/12.
 */

@Dao
interface PicDAO {

    @Query("SELECT * FROM PIC WHERE widgetId = :widgetId LIMIT 1")
    fun loadByWidgetId(widgetId: Int) : LiveData<PicItem>

    @Query("SELECT * FROM PIC")
    fun loadAllPics() : LiveData<List<PicItem>>

    @Insert
    fun insertPic(picItem: PicItem)

    @Delete
    fun deletePic(picItem: PicItem)
}