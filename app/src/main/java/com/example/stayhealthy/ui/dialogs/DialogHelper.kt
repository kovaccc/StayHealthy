package com.example.stayhealthy.ui.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import com.example.stayhealthy.R

object DialogHelper {


    private var canShow = true

    fun createLoadingDialog(activity: Activity): AlertDialog {
        return AlertDialog.Builder(activity).setCancelable(false)
                .setView(R.layout.layout_loading_dialog).create()
    }


    fun promptDialog(
            context: Context,
            message: String,
            callback: () -> Unit,
            positiveText: Int = R.string.ok,
            negativeText: Int = R.string.cancel,
            title: Int = 0,
            canShow: Boolean = true
    ) {
        this.canShow = canShow
        safeShow {
            val builder = AlertDialog.Builder(context)
                    .setMessage(message)
                    .setNegativeButton(negativeText, null)
                    .setPositiveButton(positiveText) { _, _ ->
                        callback()
                    }.setCancelable(false)
            if (title != 0) builder.setTitle(title)
            builder
        }
    }


    private fun safeShow(builder: () -> AlertDialog.Builder): AlertDialog? {
        return if (canShow) {
            canShow = false
            return builder()
                    .setOnDismissListener { canShow = true }
                    .create()
                    .also { it.show() }
        } else null
    }


}