package com.gmail.sleepybee410.picshelf

import android.Manifest
import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.gmail.sleepybee410.picshelf.Activity.EditActivity
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.pic_shelf_app_widget_configure.*
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * The configuration screen for the [PicShelfAppWidgetProvider] AppWidget.
 */
class PicShelfAppWidgetConfigureActivity : Activity() {

    private var context: Context = this@PicShelfAppWidgetConfigureActivity;
    var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    private var originUri: Uri? = null
    private var label: String = ""
    private var color: String = ""
    private var idx: Int = 0

    companion object {
        const val REQUEST_SELECT = 100
        const val RESULT_EDIT = 200

        private const val PREFS_NAME = "com.gmail.sleepybee410.picshelf.PicShelfAppWidget"
        private const val PREF_PREFIX_KEY = "appwidget_"

    }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED)
        setContentView(R.layout.pic_shelf_app_widget_configure)



        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
//                Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show()
                var intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/jpg"
                startActivityForResult(
                    Intent.createChooser(intent, "SELECT PIC"),
                    REQUEST_SELECT
                )
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
//                Toast.makeText( F
//                    this,
//                    "Permission Denied : " + deniedPermissions.toString(),
//                    Toast.LENGTH_LONG
//                ).show()
            }

        }

        btn_load_widget.setOnClickListener {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

//        Toast.makeText(this, "onactivityresult : "+data!!.data + "\nresultcode : "+resultCode, Toast.LENGTH_SHORT).show()
        when (resultCode) {
            RESULT_OK -> {
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
                                Environment.DIRECTORY_PICTURES + File.separator + "PicShelf"
                            )
                            if (!destUri.exists()) {
                                destUri.mkdirs()
                            }

                            var option = UCrop.Options()
                            option.setCompressionFormat(Bitmap.CompressFormat.JPEG)

                            UCrop.of(data.data!!, Uri.fromFile(File(destUri, srcName)))
                                .withOptions(option)
                                .start(this)
                        }
                    }
                    UCrop.REQUEST_CROP -> {
                        // Find the widget id from the intent.
                        val intentGiven = intent
                        val extras = intentGiven.extras

                        if (extras != null) {
                            appWidgetId = extras.getInt(
                                AppWidgetManager.EXTRA_APPWIDGET_ID,
                                AppWidgetManager.INVALID_APPWIDGET_ID
                            )

                            Log.d("SB", "appwidgetid : " + appWidgetId);
                        }

                        // If this activity was started with an intent without an app widget ID, finish with an error.
                        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                            finish()
                            return
                        }

                        val intentWillSended = Intent(this, EditActivity::class.java)
                        intentWillSended.putExtra("idx", idx)
                        intentWillSended.putExtra("widgetId", appWidgetId)
                        intentWillSended.putExtra("originUri", originUri)
                        intentWillSended.putExtra("uri", UCrop.getOutput(data!!))
                        intentWillSended.putExtra("label", label)
                        intentWillSended.putExtra("color", color)
                        intentWillSended.putExtra("frame", "no")
                        startActivityForResult(intentWillSended, 200)
                        Log.i("SB", "CROP DATA : " + UCrop.getOutput(data!!))
                    }
                }
            }

            UCrop.RESULT_ERROR -> {
                Toast.makeText(this, "CROP ERR : " + UCrop.getError(data!!), Toast.LENGTH_SHORT)
                    .show()
                Log.i("SB", "CROP ERR : " + UCrop.getError(data!!))

            }

            RESULT_EDIT -> {
////                var item : ListItem = data!!.getParcelableExtra("item")
////                Toast.makeText(this, "OKAY : ${item.toString()}", Toast.LENGTH_SHORT).show()
//                initList()

                // It is the responsibility of the configuration activity to update the app widget
                val appWidgetManager = AppWidgetManager.getInstance(context)
                PicShelfAppWidgetProvider.updateAppWidget(context, appWidgetManager, appWidgetId)

                //            PicShelfAppWidget.Companion.updateAppWidget$app(context, appWidgetManager, mAppWidgetId);

                val resultValue = Intent()
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                setResult(RESULT_OK, resultValue)
                finish()
            }
        }
    }
}