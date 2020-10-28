package com.gmail.sleepybee410

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import com.gmail.sleepybee410.picshelf.Activity.MainActivity
import com.gmail.sleepybee410.picshelf.R
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.android.synthetic.main.dialog_finish.*

/**
 * Created by leeseulbee on 2020/10/28.
 */
class FinishDialog(context: Context, confirmListener: View.OnClickListener) : Dialog(context), View.OnClickListener {
    private val confirmListener = confirmListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_finish)

        setCanceledOnTouchOutside(false)

        btn_review_finish.setOnClickListener(this)
        btn_cancel_finish.setOnClickListener(this)
        btn_confirm_finish.setOnClickListener(confirmListener)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_review_finish -> {
                launchReviewFlow()
            }
            R.id.btn_cancel_finish-> {
                dismiss()
            }
        }
    }

    private fun launchReviewFlow() {
    }
}