package com.gmail.sleepybee410.picshelf.activity

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
import com.gmail.sleepybee410.picshelf.WidgetProvider
import com.gmail.sleepybee410.picshelf.R
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_add.*
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * The configuration screen for the [WidgetProvider] AppWidget.
 */
class AddActivity : Activity() {

    private var context: Context = this@AddActivity;
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
        setContentView(R.layout.activity_add)
        setActionBar(toolbar_add)

        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
//                Toast.makeText(context, "Permission Granted", Toast.LENGTH_LONG).show()
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/jpg"
                startActivityForResult(
                    Intent.createChooser(intent, "SELECT PIC"),
                    REQUEST_SELECT
                )
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                finish()
            }
        }

        TedPermission.with(this)
            .setPermissionListener(permissionListener)
            .setRationaleMessage(getString(R.string.msg_permission))
            .setDeniedMessage(getString(R.string.msg_permission_denied))
//                .setPermissions(Manifest.permission_group.STORAGE)
            .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check()

//        val gridItems : ArrayList<PicItem> = GlobalUtils.loadAll(this)
//        gv_history_configure.numColumns = 3
//        gv_history_configure.adapter = HistoryGridAdapter(this, gridItems)

//        btn_load_configure.setOnClickListener {
//            var intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.addCategory(Intent.CATEGORY_OPENABLE)
//            intent.type = "image/jpg"
//            startActivityForResult(
//                Intent.createChooser(intent, "SELECT PIC"),
//                REQUEST_SELECT
//            )
//        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

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
                            option.useSourceImageAspectRatio()
                            option.setToolbarTitle(getString(R.string.edit_picture))
                            option.setToolbarColor(getColor(R.color.colorPrimary))
                            option.setStatusBarColor(getColor(R.color.colorPrimary))
                            option.setActiveWidgetColor(getColor(R.color.colorAccent))

                            UCrop.of(data.data!!, Uri.fromFile(File(destUri, srcName)))
                                .withOptions(option)
                                .start(this)
                        } else {
                            finish()
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
                WidgetProvider.updateAppWidget(context, appWidgetManager, appWidgetId)

                //            PicShelfAppWidget.Companion.updateAppWidget$app(context, appWidgetManager, mAppWidgetId);

                val resultValue = Intent()
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                setResult(RESULT_OK, resultValue)
                finish()
            }
        }
    }

}