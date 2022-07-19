package com.gmail.sleepybee410.picshelf.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.gmail.sleepybee410.picshelf.PicItem
import com.gmail.sleepybee410.picshelf.database.PicRepository

/**
 * Created by leeseulbee on 2022/07/12.
 */
class PicViewModel(application: Application): AndroidViewModel(application) {
    private val picRepository: PicRepository = PicRepository(application)

    fun insertPic(picItem: PicItem) {
        picRepository.insertPic(picItem)
    }

    fun getPicList(): LiveData<List<PicItem>> {
        return picRepository.getPicList()
    }

    fun getPicByWidgetId(widgetId: Int) : LiveData<PicItem> {
        return picRepository.getPicByWidgetId(widgetId)
    }

    fun deletePic(picItem: PicItem) {
        picRepository.deletePic(picItem)
    }
}