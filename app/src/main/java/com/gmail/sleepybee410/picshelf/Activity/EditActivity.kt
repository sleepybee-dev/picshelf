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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        idx = intent.extras.get("idx") as Int
        widgetId = intent.extras.get("widgetId") as Int
        uri = intent.extras.get("uri") as Uri
        originUri = intent.extras.get("originUri") as Uri
        label = intent.extras.get("label") as String
        color = intent.extras.get("color") as String

        Log.i("SB", "originUri : $originUri")

        et_label_edit.setText(label)

        val mgr = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        et_label_edit.post(Runnable {
            et_label_edit.isFocusableInTouchMode = true
            et_label_edit.requestFocus()

            mgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        })

        et_label_edit.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                //Enter key Action
                return if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    mgr.hideSoftInputFromWindow(v.rootView.windowToken, 0)
                    saveCropPic(v)
                    true
                } else false
            }
        })

        btn_confirm_edit.setOnClickListener { view ->
            saveCropPic(view);
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
        resultItem = PicItem(idx, widgetId, originUri, uri, label, et_label_edit.text.toString())
//        intent.putExtra("item", item!!)
        val helper = SQLiteHelper(this)
        val db = helper.writableDatabase

        if(idx==0){
            db.execSQL("INSERT INTO PICS_TB" +
                    " (widgetId, originUri, uri, label, color)" +
                    " VALUES ('$widgetId', '$originUri', '$uri', '$label', '$color')")
        }else{
            db.execSQL("INSERT OR REPLACE INTO PICS_TB" +
                    " (idx, widgetId, originUri, uri, label, color)" +
                    " VALUES ('$idx', '$widgetId', '$originUri', '$uri', '$label', '$color')")
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