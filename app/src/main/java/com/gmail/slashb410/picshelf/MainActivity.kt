package com.gmail.slashb410.picshelf

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.yalantis.ucrop.UCrop

import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        var permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                Toast.makeText(this@MainActivity, "Permission Granted", Toast.LENGTH_LONG).show()
                var intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/jpeg"
                startActivityForResult(Intent.createChooser(intent, "SELECT PIC"), 100)
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

    var originUri : Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

//        Toast.makeText(this, "onactivityresult : "+data!!.data + "\nresultcode : "+resultCode, Toast.LENGTH_SHORT).show()
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    100 -> {
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
//                        .withAspectRatio(6.0F, 4.0F)
                                .withOptions(option)
                                .start(this)

                        }

                    }
                    UCrop.REQUEST_CROP -> {

                        var intent = Intent(this, EditActivity::class.java)
                        intent.putExtra("uri", UCrop.getOutput(data!!))
                        intent.putExtra("originUri", originUri)
                        startActivityForResult(intent, 200)
                        Log.i("SB", "CROP DATA : " + UCrop.getOutput(data!!))
                    }

                    2 -> {
                        Toast.makeText(this, "OKAY : " + data!!.data.toString(), Toast.LENGTH_SHORT).show()
                    }
                }

            }

            UCrop.RESULT_ERROR -> {
                Toast.makeText(this, "CROP ERR : " + UCrop.getError(data!!), Toast.LENGTH_SHORT).show()
                Log.i("SB", "CROP ERR : " + UCrop.getError(data!!))

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

//    companion object {
//         SRC_URI
//        const val DEST_URI = ""
//    }
}
