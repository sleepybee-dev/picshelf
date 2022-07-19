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
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.viewModelFactory
import com.gmail.sleepybee410.picshelf.*
import com.gmail.sleepybee410.picshelf.activity.AddActivity.Companion.RESULT_EDIT
import com.gmail.sleepybee410.picshelf.databinding.ActivityEditBinding
import com.gmail.sleepybee410.picshelf.viewmodel.PicViewModel
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDateTime


class EditActivity : AppCompatActivity() {

    lateinit var binding : ActivityEditBinding
    lateinit var picViewModel: PicViewModel

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit);
        picViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory())[PicViewModel::class.java]

        setSupportActionBar(binding.toolbarEdit)
    }

    override fun onResume() {
        super.onResume()

        widgetId = intent.extras!!.get("widgetId") as Int

        if (widgetId != null)
            picViewModel.getPicByWidgetId(widgetId).observe(this) {
                resultItem = it

                if (resultItem == null) {
                    idx = intent.extras!!.get("idx") as Int
                    uri = intent.extras!!.get("uri") as Uri
                    originUri = intent.extras!!.get("originUri") as Uri
                    label = intent.extras!!.get("label") as String
                    color = intent.extras!!.get("color") as String
                } else {
                    idx = resultItem!!.idx
                    uri = resultItem!!.uri!!
                    originUri = resultItem!!.originUri!!
                    label = resultItem!!.label!!
                    color = resultItem!!.color!!
                    frame = resultItem!!.frame!!
                }

                val splitUri = uri.toString().split("/")
                val uriLength = splitUri.size


                binding.tvUriEdit.text =
                    "/${splitUri[uriLength - 3]}/${splitUri[uriLength - 2]}/${splitUri.last()}"

                binding.etLabelEdit
                    .setText(label)
                binding.etLabelEdit.setSelection(label.length)
                binding.etLabelEdit.setOnEditorActionListener { v, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        return@setOnEditorActionListener true
                    }
                    return@setOnEditorActionListener false
                }

                binding.rbNoFrameEdit.isChecked = frame == "no"
                binding.rbDefaultEdit.isChecked = frame == "default"
                binding.rbPolaroidEdit.isChecked = frame == "polaroid"

                when (frame) {
                    "no" -> {
                        binding.flFrameEdit.setPadding(0, 0, 0, 0)
                        binding.tvFrameEdit.visibility = View.GONE
                    }
                    "default" -> {
                        binding.flFrameEdit.setPadding(16, 16, 16, 16)
                        binding.tvFrameEdit.visibility = View.GONE
                    }
                    "polaroid" -> {
                        binding.flFrameEdit.setPadding(16, 16, 16, 80)
                        binding.tvFrameEdit.visibility = View.VISIBLE
                    }
                }

                binding.rgFrameEdit.setOnCheckedChangeListener { group, checkedId ->
                    when (checkedId) {
                        R.id.rb_no_frame_edit -> {
                            frame = "no"
                            binding.tvFrameEdit.visibility = View.GONE
                            binding.flFrameEdit.setPadding(0, 0, 0, 0)
                        }
                        R.id.rb_default_edit -> {
                            frame = "default"
                            binding.tvFrameEdit.visibility = View.GONE
                            binding.flFrameEdit.setPadding(16, 16, 16, 16)
                        }
                        R.id.rb_polaroid_edit -> {
                            frame = "polaroid"
                            binding.tvFrameEdit.visibility = View.VISIBLE
                            binding.tvFrameEdit.text = binding.etLabelEdit.text
                            binding.flFrameEdit.setPadding(16, 16, 16, 80)
                        }
                    }
                }

                val mgr = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                binding.etLabelEdit.post(Runnable {
                    binding.etLabelEdit.isFocusableInTouchMode = true
                    binding.etLabelEdit.requestFocus()

                    mgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                })

                binding.etLabelEdit.setOnKeyListener { v, keyCode, event -> //Enter key Action
                    if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                        mgr.hideSoftInputFromWindow(v.rootView.windowToken, 0)
                        saveCropPic(v)
                        true
                    } else false
                }

                binding.etLabelEdit.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        binding.tvFrameEdit.text = s.toString()
                    }

                    override fun afterTextChanged(s: Editable?) {
                    }
                })

                binding.btnConfirmEdit.setOnClickListener { view ->
                    if (binding.etLabelEdit.text.isEmpty()) {
                        Snackbar.make(view, "Please Input Label", Snackbar.LENGTH_LONG).show();
                    } else {
                        saveCropPic(view);
                    }
                }

                if (uri != null) {
                    Log.i("SB", "EDIT uri : " + uri!!.toString())

                    Glide.with(this)
                        .load(uri)
                        .into(binding.ivEdit)
                }
            }
    }

    override fun onPause() {
        super.onPause()
        widgetId = 0
        resultItem = null
    }

    private fun saveCropPic(view : View) {
        val today = LocalDateTime.now().toString()

        label = binding.etLabelEdit.text.toString()

        var intent = Intent()
        resultItem = PicItem(idx, today, widgetId, originUri, uri, label, binding.etLabelEdit.text.toString(), frame)
//        intent.putExtra("item", item!!)

        if (resultItem != null) {
            picViewModel.insertPic(resultItem!!)
        }

        if (idx != 0) {
            val appWidgetManager = AppWidgetManager.getInstance(this)
            WidgetProvider.updateAppWidget(this, appWidgetManager, widgetId)
        }

        setResult(RESULT_EDIT, intent)
        hideKeyboard(currentFocus ?: View(this))

        Snackbar.make(view, "SAVE", Snackbar.LENGTH_SHORT).show()
        finish()
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}