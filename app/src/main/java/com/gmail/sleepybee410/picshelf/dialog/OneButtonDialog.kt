package com.gmail.sleepybee410.picshelf.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.gmail.sleepybee410.picshelf.R
import kotlinx.android.synthetic.main.dialog_one_button.*

/**
 * Created by leeseulbee on 2020/11/24.
 */
class OneButtonDialog(context: Context, private val confirmListener: View.OnClickListener)  : Dialog(context), View.OnClickListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_one_button)

        btn_cancel_one_button.setOnClickListener(this)
        btn_confirm_one_button.setOnClickListener(confirmListener)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_cancel_one_button -> {
                dismiss()
            }
        }

    }
}