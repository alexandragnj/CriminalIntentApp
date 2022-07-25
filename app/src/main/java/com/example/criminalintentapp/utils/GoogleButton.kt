package com.example.criminalintentapp.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

class GoogleButton(context: Context, attrs: AttributeSet): AppCompatButton(context,attrs) {
    init{
        applyFont()
    }

    private fun applyFont(){
        //This is used to get the file from the assets folder and set it up to the title textView
        val typeface: Typeface = Typeface.createFromAsset(context.assets, "Arial Unicode.ttf")
        setTypeface(typeface)
    }
}