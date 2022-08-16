package com.gmail.sleepybee410.picshelf.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.sleepybee410.picshelf.ConfirmDialog
import com.gmail.sleepybee410.picshelf.adapter.PicListAdapter
import com.gmail.sleepybee410.picshelf.R
import com.gmail.sleepybee410.picshelf.databinding.ActivityMainBinding
import com.gmail.sleepybee410.picshelf.viewmodel.PicViewModel
import com.yalantis.ucrop.UCrop
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var picViewModel: PicViewModel
    private lateinit var picAdapter: PicListAdapter

    companion object {
        const val REQUEST_SELECT = 100
        const val RESULT_EDIT = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbarMain)

        binding.lifecycleOwner = this

        picViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(PicViewModel::class.java)


//        MobileAds.initialize(this) {}

//        val adRequest = AdRequest.Builder().build()
//        binding.includeMain.avMain.loadAd(adRequest)
    }

    override fun onResume() {
        super.onResume()
        initList()
    }

    private fun initList() {

        picViewModel.getPicList().observe(this, Observer {
            picAdapter = PicListAdapter(it)
            binding.includeMain.rvMain.visibility = if(it.isEmpty()) View.GONE else View.VISIBLE
            binding.includeMain.tvNoHistoryMain.visibility = if(it.isEmpty()) View.VISIBLE else View.GONE
        })

        val mLayoutManager = LinearLayoutManager(this)
        binding.includeMain.rvMain.layoutManager = mLayoutManager
        binding.includeMain.rvMain.adapter = picAdapter
    }

    private var originUri : Uri? = null
    private var label : String = ""
    private var color : String = ""
    private var idx : Int = 0

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//        Toast.makeText(this, "onactivityresult : "+data!!.data + "\nresultcode : "+resultCode, Toast.LENGTH_SHORT).show()
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    REQUEST_SELECT -> {
                        Toast.makeText(this, "SEL IMG : " + data!!.data, Toast.LENGTH_SHORT).show()
                        if (data!!.data != null) {
                            originUri = data.data
                            val current = LocalDateTime.now()
                            val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                            val formatted = current.format(formatter)
                            var srcName = "$formatted.jpg"
                            var destUri = File(
                                Environment.getExternalStorageDirectory(),
                                Environment.DIRECTORY_DCIM + File.separator + "PicShelf"
                            )
                            if (!destUri.exists()) {
                                destUri.mkdirs()
                            }

                            var option = UCrop.Options()
                            option.setCompressionFormat(Bitmap.CompressFormat.JPEG)

                            UCrop.of(data.data!!, Uri.fromFile(File(destUri, srcName)))
                                .withAspectRatio(5.0F, 3.0F)
                                .withOptions(option)
                                .start(this)
                        }
                    }
                    UCrop.REQUEST_CROP -> {

                        var intent = Intent(this, EditActivity::class.java)
                        intent.putExtra("idx", idx)
                        intent.putExtra("originUri", originUri)
                        intent.putExtra("uri", UCrop.getOutput(data!!))
                        intent.putExtra("label", label)
                        intent.putExtra("color", color)
                        startActivityForResult(intent, 200)
                        Log.i("SB", "CROP DATA : " + UCrop.getOutput(data!!))
                    }
                }
            }

            UCrop.RESULT_ERROR -> {
                Toast.makeText(this, "CROP ERR : " + UCrop.getError(data!!), Toast.LENGTH_SHORT).show()
                Log.i("SB", "CROP ERR : " + UCrop.getError(data!!))

            }

            RESULT_EDIT -> {
//                var item : ListItem = data!!.getParcelableExtra("item")
//                Toast.makeText(this, "OKAY : ${item.toString()}", Toast.LENGTH_SHORT).show()
                initList()
            }
        }
    }

    override fun onBackPressed() {
        val finishDialog = ConfirmDialog(this,
            View.OnClickListener {
                finish()
            })
        finishDialog.show()
    }
}
