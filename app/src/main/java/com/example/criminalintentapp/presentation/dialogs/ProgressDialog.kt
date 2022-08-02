package com.example.criminalintentapp.presentation.dialogs

import android.app.Activity
import android.app.Dialog
import com.example.criminalintentapp.R

class ProgressDialog(activity: Activity){

    private var progressDialog: Dialog= Dialog(activity)

    fun showProgressDialog() {
        progressDialog.setContentView(R.layout.dialog_progress)

        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

        progressDialog.show()
    }

    fun hideProgressDialog() {
        progressDialog.dismiss()
    }
}