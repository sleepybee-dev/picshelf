package com.gmail.sleepybee410.picshelf.activity

import android.Manifest
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.gmail.sleepybee410.picshelf.WidgetProvider
import com.gmail.sleepybee410.picshelf.R
import com.gmail.sleepybee410.picshelf.databinding.ActivityAddBinding
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.yalantis.ucrop.UCrop
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * The configuration screen for the [WidgetProvider] AppWidget.
 */
class AddActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddBinding
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

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add);
        setActionBar(binding.toolbarAdd)

        setResult(RESULT_CANCELED)
        val permissionGrantedResultLauncher: ActivityResultLauncher<Intent> =
            getPermissionGrantedResultLauncher()

        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
//                Toast.makeText(context, "Permission Granted", Toast.LENGTH_LONG).show()
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/jpg"

                permissionGrantedResultLauncher.launch(Intent.createChooser(intent, "SELECT PIC"))
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                finish()
            }
        }

        TedPermission.create()
            .setPermissionListener(permissionListener)
            .setRationaleMessage(getString(R.string.msg_permission))
            .setDeniedMessage(getString(R.string.msg_permission_denied))
            .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check()

    }

    private fun getPermissionGrantedResultLauncher(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val intent = it.data
                if (intent != null) {
                    return@registerForActivityResult
                }

                val data = intent?.data
                Toast.makeText(this, "SEL IMG : " + data, Toast.LENGTH_SHORT).show()
                if (data != null) {
                    originUri = data
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

                    val cropResultLauncher = getCropResultLauncher()

                    val intent = UCrop.of(data, Uri.fromFile(File(destUri, srcName)))
                        .withOptions(option)
                        .getIntent(this@AddActivity)

                    cropResultLauncher.launch(intent)

                } else {
                    finish()
                }
            }
        }
    }

    private fun getCropResultLauncher(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val intent = it.data
                if (intent != null) {
                    return@registerForActivityResult
                }

                // Find the widget id from the intent.
                val intentGiven = intent
                val extras = intentGiven?.extras

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
                }

                val editResultLauncher = getEditResultLauncher()

                val intentWillSended = Intent(this, EditActivity::class.java)
                intentWillSended.putExtra("idx", idx)
                intentWillSended.putExtra("widgetId", appWidgetId)
                intentWillSended.putExtra("originUri", originUri)
                intentWillSended.putExtra("uri", UCrop.getOutput(intent!!))
                intentWillSended.putExtra("label", label)
                intentWillSended.putExtra("color", color)
                intentWillSended.putExtra("frame", "no")
                startActivityForResult(intentWillSended, 200)
                Log.i("SB", "CROP DATA : " + UCrop.getOutput(intent!!))

                editResultLauncher.launch(intentWillSended)
            }
        }
    }

    private fun getEditResultLauncher(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            val appWidgetManager = AppWidgetManager.getInstance(this@AddActivity)
            WidgetProvider.updateAppWidget(this@AddActivity, appWidgetManager, appWidgetId)

            val resultValue = Intent()
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(RESULT_OK, resultValue)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            RESULT_OK -> {
                when (requestCode) {
                    UCrop.REQUEST_CROP -> {

                    }
                }
            }

            UCrop.RESULT_ERROR -> {
                Toast.makeText(this, "CROP ERR : " + UCrop.getError(data!!), Toast.LENGTH_SHORT)
                    .show()
                Log.i("SB", "CROP ERR : " + UCrop.getError(data!!))

            }
        }
    }

}