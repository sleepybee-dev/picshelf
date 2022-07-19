package com.gmail.sleepybee410.picshelf.database

import android.app.Application
import androidx.lifecycle.LiveData
import com.gmail.sleepybee410.picshelf.PicItem
import java.lang.Appendable

/**
 * Created by leeseulbee on 2022/07/12.
 */
class PicRepository(application: Application) {
    private var picDatabase: PicDatabase = PicDatabase.getInstance(application)
    private var picDao: PicDAO = picDatabase.picDao()
    lateinit var picItems: LiveData<List<PicItem>>

    fun getPicList(): LiveData<List<PicItem>> {
        picItems = picDao.loadAllPics()
        return picItems
    }

    fun getPicByWidgetId(widgetId: Int) : LiveData<PicItem> {
        return picDao.loadByWidgetId(widgetId)
    }

    fun insertPic(picItem: PicItem) {
        Thread {
            picDao.insertPic(picItem)
        }.start()
    }

    fun deletePic(picItem: PicItem) {
        Thread {
            picDao.deletePic(picItem)
        }.start()
    }
}