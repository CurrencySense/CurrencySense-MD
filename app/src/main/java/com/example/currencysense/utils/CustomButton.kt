package com.example.currencysense.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.currencysense.R

class CustomButton : AppCompatButton {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private var txtColor: Int = 0
    private var enabledBackground: Drawable

    init {
        txtColor = ContextCompat.getColor(context, android.R.color.white)
        enabledBackground = ContextCompat.getDrawable(context, R.drawable.history_button) as Drawable
    }

    fun setButtonText(text: String) {
        this.text = text
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = enabledBackground
        setTextColor(txtColor)
        textSize = 24f
        gravity = Gravity.CENTER
    }
}