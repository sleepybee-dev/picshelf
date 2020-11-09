package com.gmail.sleepybee410.picshelf.Activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.gmail.sleepybee410.picshelf.GlobalUtils
import com.gmail.sleepybee410.picshelf.PicItem
import com.gmail.sleepybee410.picshelf.R
import kotlinx.android.synthetic.main.activity_single_image.*

/**
 * Created by leeseulbee on 2020/11/09.
 */
class SingleImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_image)

        val idx = intent.extras!!.get("dbIdx") as Int
        val item = GlobalUtils.loadByDBIdx(this, idx)

        if(item != null) {
            Glide.with(this).load(item.uri).into(iv_single_image)
            tv_title_single_image.text = item.label
            tv_date_single_image.text = item.createDate

            btn_select_single_image.setOnClickListener{
                val intent = Intent(this, EditActivity::class.java)
                intent.putExtra("widgetId", item.widgetId)
                intent.putExtra("dbIdx", item.idx)
                startActivity(intent)
            }
            btn_cancel_single_image.setOnClickListener{
                finish()
            }
        }
    }
}