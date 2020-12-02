package com.gmail.sleepybee410.picshelf.activity

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_edit.*
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.gmail.sleepybee410.picshelf.*
import com.gmail.sleepybee410.picshelf.activity.AddActivity.Companion.RESULT_EDIT
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDateTime


class EditActivity : AppCompatActivity() {

    var resultItem : PicItem? = null
    var idx : Int = 0
    var widgetId : Int = 0
    var dbIdx : Int? = 0
    lateinit var uri : Uri
    lateinit var originUri : Uri
    var label : String = ""
    var color : String = "red"
    var frame : String = "no"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        setSupportActionBar(toolbar_edit)
    }

    override fun onResume() {
        super.onResume()

        widgetId = intent.extras!!.get("widgetId") as Int

        if (widgetId != null)
            resultItem = GlobalUtils.loadByWidgetId(this, widgetId!!)

        if(resultItem == null) {
            idx = intent.extras.get("idx") as Int
            uri = intent.extras.get("uri") as Uri
            originUri = intent.extras.get("originUri") as Uri
            label = intent.extras.get("label") as String
            color = intent.extras.get("color") as String
        } else {
            idx = resultItem!!.idx
            uri = resultItem!!.uri
            originUri = resultItem!!.originUri
            label = resultItem!!.label
            color = resultItem!!.color
            frame = resultItem!!.frame
        }

        val splitUri = uri.toString().split("/")
        val uriLength = splitUri.size
        tv_uri_edit.text =
            "/${splitUri[uriLength - 3]}/${splitUri[uriLength - 2]}/${splitUri.last()}"

        et_label_edit.setText(label)
        et_label_edit.setSelection(label.length)
        et_label_edit.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        rb_no_frame_edit.isChecked = frame == "no"
        rb_default_edit.isChecked = frame == "default"
        rb_polaroid_edit.isChecked = frame == "polaroid"

        when (frame) {
            "no" -> {
                fl_frame_edit.setPadding(0,0,0,0)
                tv_frame_edit.visibility = View.GONE
            }
            "default" -> {
                fl_frame_edit.setPadding(16, 16, 16, 16)
                tv_frame_edit.visibility = View.GONE
            }
            "polaroid" -> {
                fl_frame_edit.setPadding(16, 16, 16, 80)
                tv_frame_edit.visibility = View.VISIBLE
            }
        }

        rg_frame_edit.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_no_frame_edit -> {
                    frame = "no"
                    tv_frame_edit.visibility = View.GONE
                    fl_frame_edit.setPadding(0,0,0,0)
                }
                R.id.rb_default_edit -> {
                    frame = "default"
                    tv_frame_edit.visibility = View.GONE
                    fl_frame_edit.setPadding(16, 16, 16, 16)
                }
                R.id.rb_polaroid_edit -> {
                    frame = "polaroid"
                    tv_frame_edit.visibility = View.VISIBLE
                    tv_frame_edit.text = et_label_edit.text
                    fl_frame_edit.setPadding(16, 16, 16, 80)
                }
            }
        }

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

        et_label_edit.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tv_frame_edit.text = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        btn_confirm_edit.setOnClickListener { view ->
            if (et_label_edit.text.isEmpty()){
                Snackbar.make(view, "Please Input Label", Snackbar.LENGTH_LONG).show();
            } else {
                saveCropPic(view);
            }
        }

        if (uri != null) {
            Log.i("SB", "EDIT uri : " + uri!!.toString())

            Glide.with(this)
                .load(uri)
                .into(iv_edit)
        }
    }

    override fun onPause() {
        super.onPause()
        widgetId = 0
        resultItem = null
    }

    private fun saveCropPic(view : View) {
        val today = LocalDateTime.now().toString()

        label = et_label_edit.text.toString()

        Snackbar.make(view, "SAVE", Snackbar.LENGTH_SHORT).show()
        var intent = Intent()
        resultItem = PicItem(idx, today, widgetId, originUri, uri, label, et_label_edit.text.toString(), frame)
//        intent.putExtra("item", item!!)
        val helper = SQLiteHelper(this)
        val db = helper.writableDatabase

        if(idx==0){
            db.execSQL("INSERT INTO PICS_TB" +
                    " (createDate, widgetId, originUri, uri, label, color, frame)" +
                    " VALUES ('$today', '$widgetId', '$originUri', '$uri', '$label', '$color', '$frame')")

        }else{
            db.execSQL("INSERT OR REPLACE INTO PICS_TB" +
                    " (idx, createDate, widgetId, originUri, uri, label, color, frame)" +
                    " VALUES ('$idx', '$today', '$widgetId', '$originUri', '$uri', '$label', '$color', '$frame')")

            val appWidgetManager = AppWidgetManager.getInstance(this)
            WidgetProvider.updateAppWidget(this, appWidgetManager, widgetId)
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