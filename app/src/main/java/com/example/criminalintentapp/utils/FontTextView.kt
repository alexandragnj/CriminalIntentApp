package com.example.criminalintentapp.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class FontTextView(context: Context, attrs: AttributeSet): AppCompatTextView(context,attrs) {
    init{
        applyFont()
    }

    private fun applyFont(){
        val typeface: Typeface = Typeface.createFromAsset(context.assets, "American Captain.ttf")
        setTypeface(typeface)
    }
}