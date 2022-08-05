package com.example.criminalintentapp.presentation.dialogs

import android.app.Activity
import android.app.Dialog
import com.example.criminalintentapp.R

class ProgressDialog(activity: Activity) {

    private var progressDialog = Dialog(activity).apply {
        setContentView(R.layout.dialog_progress)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }

    fun show() {
        progressDialog.show()
    }

    fun hide() {
        progressDialog.dismiss()
    }
}