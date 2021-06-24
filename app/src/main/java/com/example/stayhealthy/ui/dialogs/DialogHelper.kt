package com.example.stayhealthy.ui.dialogs

import android.app.Activity
import android.app.AlertDialog
import com.example.stayhealthy.R

object DialogHelper {
    fun createLoadingDialog(activity: Activity): AlertDialog {
        return AlertDialog.Builder(activity).setCancelable(false)
                .setView(R.layout.layout_loading_dialog).create()
    }
}