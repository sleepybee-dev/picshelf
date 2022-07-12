package com.gmail.sleepybee410.picshelf.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.gmail.sleepybee410.picshelf.activity.AddActivity.Companion.RESULT_EDIT
import com.gmail.sleepybee410.picshelf.GlobalUtils
import com.gmail.sleepybee410.picshelf.R
import com.gmail.sleepybee410.picshelf.databinding.ActivitySingleImageBinding

/**
 * Created by leeseulbee on 2020/11/09.
 */
class SingleImageActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySingleImageBinding
    private val REQUEST_EDIT = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_single_image)

        val idx = intent.extras!!.get("widgetId") as Int
        val item = GlobalUtils.loadByWidgetId(this, idx)

        if(item != null) {
            Glide.with(this).load(item.uri).into(binding.ivSingleImage)
            binding.tvTitleSingleImage.text = item.label
            binding.tvDateSingleImage.text = item.createDate

            binding.btnSelectSingleImage.setOnClickListener{
                val intent = Intent(this, EditActivity::class.java)
                intent.putExtra("widgetId", item.widgetId)
                startActivityForResult(intent, REQUEST_EDIT)
            }
            binding.btnCancelSingleImage.setOnClickListener{
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_EDIT) {
            setResult(RESULT_EDIT)
        }
        finish()
    }
}