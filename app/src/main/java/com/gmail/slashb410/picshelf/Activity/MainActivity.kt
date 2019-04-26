package com.gmail.slashb410.picshelf.Activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.gmail.slashb410.picshelf.ListAdapter
import com.gmail.slashb410.picshelf.ListItem
import com.gmail.slashb410.picshelf.R
import com.gmail.slashb410.picshelf.SQLiteHelper
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.yalantis.ucrop.UCrop

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.pic_shelf_app_widget.view.*
import java.io.File


class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_SELECT = 100
        const val RESULT_EDIT = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //화면비율가져오기

        initList()

        var permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                Toast.makeText(this@MainActivity, "Permission Granted", Toast.LENGTH_LONG).show()
                var intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/jpeg"
                startActivityForResult(Intent.createChooser(intent, "SELECT PIC"), REQUEST_SELECT)
//            startActivityForResult(intent, 100)
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(
                    this@MainActivity,
                    "Permission Denied : " + deniedPermissions.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }

        }

        fab.setOnClickListener {

            TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("저장소 접근 권한 필요")
                .setDeniedMessage("[설정]-[권한]에서 허용 가능")
//                .setPermissions(Manifest.permission_group.STORAGE)
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check()
        }
    }

    private fun initList() {

        //preference에서 가져오기
        var list = loadData()
        var adapter : ListAdapter = ListAdapter(list)

        var mLayoutManager = LinearLayoutManager(this)
        rv_main.layoutManager = mLayoutManager
        rv_main.adapter = adapter

    }

    private fun loadData(): ArrayList<ListItem> {

        val helper = SQLiteHelper(this)
        val db = helper.writableDatabase

//        var db = this.openOrCreateDatabase("picshelf", Context.MODE_PRIVATE, null)
//        db.execSQL("CREATE TABLE IF NOT EXISTS PICS_TB;")

        var cursor = db.rawQuery("SELECT * FROM PICS_TB", null)
        var items : ArrayList<ListItem> = ArrayList()

        if(cursor!=null){
            if(cursor.moveToFirst()){
                do{
                    var idx : Int = cursor.getInt(cursor.getColumnIndex("idx"))
                    var label = cursor.getString(cursor.getColumnIndex("label"))
                    var color = cursor.getString(cursor.getColumnIndex("color"))
                    var originUri = cursor.getString(cursor.getColumnIndex("originUri"))
                    var uri = cursor.getString(cursor.getColumnIndex("uri"))

                    var item = ListItem(idx, Uri.parse(originUri), Uri.parse(uri), label, color)
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

//        Toast.makeText(this, "onactivityresult : "+data!!.data + "\nresultcode : "+resultCode, Toast.LENGTH_SHORT).show()
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    REQUEST_SELECT -> {
                        Toast.makeText(this, "SEL IMG : " + data!!.data, Toast.LENGTH_SHORT).show()
                        if (data!!.data != null) {
                            originUri = data.data
                            var srcName = File(data.data.path).name
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

}
