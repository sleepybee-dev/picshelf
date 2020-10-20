package com.gmail.sleepybee410.picshelf.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_edit.*
import android.support.design.widget.Snackbar
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RadioGroup
import com.gmail.sleepybee410.picshelf.PicItem
import com.gmail.sleepybee410.picshelf.PicShelfAppWidgetConfigureActivity.Companion.RESULT_EDIT
import com.gmail.sleepybee410.picshelf.R
import com.gmail.sleepybee410.picshelf.SQLiteHelper


class EditActivity : AppCompatActivity() {

    var resultItem : PicItem? = null
    var idx : Int = 0
    var widgetId : Int = 0
    lateinit var uri : Uri
    lateinit var originUri : Uri
    var label : String = ""
    var color : String = "red"
    var frame : String = "no"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        idx = intent.extras.get("idx") as Int
        widgetId = intent.extras.get("widgetId") as Int
        uri = intent.extras.get("uri") as Uri
        originUri = intent.extras.get("originUri") as Uri
        label = intent.extras.get("label") as String
        color = intent.extras.get("color") as String
        frame = intent.extras.get("frame") as String

        Log.i("SB", "originUri : $originUri")

        et_label_edit.setText(label)

        rb_no_frame_edit.isChecked = frame == "no"
        rb_polaroid_edit.isChecked = frame == "polaroid"

        val mgr = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        et_label_edit.post(Runnable {
            et_label_edit.isFocusableInTouchMode = true
            et_label_edit.requestFocus()

            mgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        })

        et_label_edit.setOnKeyListener { v, keyCode, event -> //Enter key Action
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                mgr.hideSoftInputFromWindow(v.rootView.windowToken, 0)
                saveCropPic(v)
                true
            } else false
        }

        btn_confirm_edit.setOnClickListener { view ->
            saveCropPic(view);
        }

        rg_frame_edit.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_no_frame_edit -> {
                    frame = "no"
                    fl_frame_edit.setPadding(0,0,0,0)
                }
                R.id.rb_polaroid_edit -> {
                    frame = "polaroid"
                    fl_frame_edit.setPadding(16, 16, 16, 72)
                }
            }
        }


        if (uri != null) {
            Log.i("SB", "EDIT uri : " + uri!!.toString())

            Glide.with(this)
                .load(uri)
                .into(iv_edit)
        }

    }

    private fun saveCropPic(view : View) {

        label = et_label_edit.text.toString()

        Snackbar.make(view, "SAVE", Snackbar.LENGTH_SHORT).show()
        var intent = Intent()
        resultItem = PicItem(idx, widgetId, originUri, uri, label, et_label_edit.text.toString(), frame)
//        intent.putExtra("item", item!!)
        val helper = SQLiteHelper(this)
        val db = helper.writableDatabase

        if(idx==0){
            db.execSQL("INSERT INTO PICS_TB" +
                    " (widgetId, originUri, uri, label, color, frame)" +
                    " VALUES ('$widgetId', '$originUri', '$uri', '$label', '$color', '$frame')")
        }else{
            db.execSQL("INSERT OR REPLACE INTO PICS_TB" +
                    " (idx, widgetId, originUri, uri, label, color, frame)" +
                    " VALUES ('$idx', '$widgetId', '$originUri', '$uri', '$label', '$color', '$frame')")
        }

        setResult(RESULT_EDIT, intent)
        hideKeyboard(currentFocus ?: View(this))
        finish()
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}