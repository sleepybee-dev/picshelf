package com.gmail.sleepybee410.picshelf

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.dialog_finish.*

/**
 * Created by leeseulbee on 2020/10/28.
 */
class ConfirmDialog(context: Context, private val confirmListener: View.OnClickListener) : Dialog(context), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_finish)

        setCanceledOnTouchOutside(false)

//        btn_review_finish.setOnClickListener(this)
        btn_cancel_finish.setOnClickListener(this)
        btn_confirm_finish.setOnClickListener(confirmListener)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
//            R.id.btn_review_finish -> {
//                launchReviewFlow()
//            }
            R.id.btn_cancel_finish -> {
                dismiss()
            }
        }
    }

    private fun launchReviewFlow() {
    }

    fun setText(message: String) {
        tv_dialog.text = message
    }
}