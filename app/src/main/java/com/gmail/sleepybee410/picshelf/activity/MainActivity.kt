package com.gmail.sleepybee410.picshelf.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.sleepybee410.picshelf.dialog.FinishDialog
import com.gmail.sleepybee410.picshelf.adapter.PicListAdapter
import com.gmail.sleepybee410.picshelf.PicItem
import com.gmail.sleepybee410.picshelf.R
import com.gmail.sleepybee410.picshelf.SQLiteHelper
import com.yalantis.ucrop.UCrop

import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_SELECT = 100
        const val RESULT_EDIT = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setSupportActionBar(toolbar)

        //화면비율가져오기
        getItemSize()
        initList()

    }

    private fun getItemSize() {

    }

    private fun initList() {
        //preference에서 가져오기
        var list = loadData()
        var adapterPic : PicListAdapter = PicListAdapter(list)

        var mLayoutManager = LinearLayoutManager(this)
        rv_main.layoutManager = mLayoutManager
        rv_main.adapter = adapterPic

    }

    private fun loadData(): ArrayList<PicItem> {

        val helper = SQLiteHelper(this)
        val db = helper.writableDatabase

//        var db = this.openOrCreateDatabase("picshelf", Context.MODE_PRIVATE, null)
//        db.execSQL("CREATE TABLE IF NOT EXISTS PICS_TB;")

        var cursor = db.rawQuery("SELECT * FROM PICS_TB", null)
        var items : ArrayList<PicItem> = ArrayList()

        if(cursor!=null){
            if(cursor.moveToFirst()){
                do{
                    var idx : Int = cursor.getInt(cursor.getColumnIndex("idx"))
                    var createDate = cursor.getString(cursor.getColumnIndex("createDate"))
                    var widgetId : Int = cursor.getInt(cursor.getColumnIndex("widgetId"))
                    var label = cursor.getString(cursor.getColumnIndex("label"))
                    var color = cursor.getString(cursor.getColumnIndex("color"))
                    var originUri = cursor.getString(cursor.getColumnIndex("originUri"))
                    var uri = cursor.getString(cursor.getColumnIndex("uri"))
                    var frame = cursor.getString(cursor.getColumnIndex("frame"))

                    var item = PicItem(idx, createDate, widgetId, Uri.parse(originUri), Uri.parse(uri), label, color, frame)
                    items.add(item)

                } while (cursor.moveToNext())
            }
        }

        db.close()

        return items

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

//    override fun onBackPressed() {
////        val manager = ReviewManagerFactory.create(this)
//        val manager = FakeReviewManager(this)
//
//        val request = manager.requestReviewFlow()
//        request.addOnCompleteListener { request ->
//            if (request.isSuccessful) {
//                // We got the ReviewInfo object
//                val reviewInfo = request.result
//                val flow = manager.launchReviewFlow(this, reviewInfo)
//                flow.addOnCompleteListener { _ ->
//                }
//            } else {
//                // There was some problem, continue regardless of the result.
//            }
//        }
//    }


    override fun onBackPressed() {
        val finishDialog = FinishDialog(this,
            View.OnClickListener {
                finish()
            })
        finishDialog.show()
    }
}
