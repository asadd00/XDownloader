package com.android.downloadanything.utils

import android.app.Activity
import android.app.Dialog
import com.android.downloadanything.R

class Methods {
    companion object{
        fun getProgressLoader(activity: Activity): Dialog{
            val dialog = Dialog(activity)
            dialog.setContentView(R.layout.dialog_progress)
            dialog.setCancelable(false)
            return dialog
        }
    }

}