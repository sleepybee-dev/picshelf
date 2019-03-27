package com.gmail.slashb410.picshelf

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


class EditActivity : AppCompatActivity() {

    var item : ListItem? = null
    lateinit var uri : Uri
    lateinit var originUri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        uri = intent.extras.get("uri") as Uri
        originUri = intent.extras.get("originUri") as Uri

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


        if (uri != null) {
            Log.i("SB", "EDIT uri : " + uri!!.toString())

            Glide.with(this)
                .load(uri)
                .into(iv_edit)

        }

    }

    private fun saveCropPic(view : View) {

        Snackbar.make(view, "SAVE", Snackbar.LENGTH_SHORT).show()
        var intent = Intent()
        item!!.originUri = originUri
        item!!.uri = uri
        item!!.color = "red"
        item!!.label = et_label_edit.text.toString()
        intent.putExtra("item", item!!)

        setResult(2, intent)
        finish()
    }


}