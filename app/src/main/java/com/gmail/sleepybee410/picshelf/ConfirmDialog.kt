package com.gmail.sleepybee410.picshelf

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import com.gmail.sleepybee410.picshelf.databinding.DialogFinishBinding


/**
 * Created by leeseulbee on 2020/10/28.
 */
class ConfirmDialog(context: Context, private val confirmListener: View.OnClickListener) : Dialog(context), View.OnClickListener {
    private lateinit var binding : DialogFinishBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(
                context
            ), R.layout.dialog_finish, null, false
        )
        setContentView(binding.getRoot())

        setCanceledOnTouchOutside(false)

//        btn_review_finish.setOnClickListener(this)
        binding.btnCancelFinish.setOnClickListener(this)
        binding.btnConfirmFinish.setOnClickListener(confirmListener)
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
        binding.tvDialog.text = message
    }
}