package com.aliyunm.weeeechat.weidget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class WidgetTextView : AppCompatTextView {

    private val mContext : Context
    private val mAttrs : AttributeSet?
    private val mDefStyleAttr : Int
    private lateinit var mRect : Rect
    private lateinit var mPaint : Paint
    // "ጿ ኈ ቼ ዽ ጿ"

    constructor(context : Context) : this(context, null)

    constructor(context : Context, attrs : AttributeSet?) : this(context, attrs, 0)

    constructor(context : Context, attrs : AttributeSet?, defStyleAttr : Int) : super(context, attrs, defStyleAttr) {
        mContext = context
        mAttrs = attrs
        mDefStyleAttr = defStyleAttr
        init()
    }

    private fun init() {
        mRect = Rect()
        mPaint = Paint()
        mPaint.apply {
            textSize = this@WidgetTextView.textSize
            getTextBounds(text, 0, text.length, mRect)
        }
    }

    override fun onDraw(canvas: Canvas) {
        measureLength()
        super.onDraw(canvas)
    }

    private fun measureLength() {
        val textPaint = TextPaint()
        textPaint.textSize = mContext.resources.displayMetrics.scaledDensity * textSize
        var displayStr: String = text.toString()
        if (textPaint.measureText(displayStr) > width) {
            while (textPaint.measureText(displayStr) >= width - textPaint.measureText("...")) {
                displayStr = displayStr.substring(0, displayStr.length - 1)
            }
            displayStr += "..."
        }
        text = displayStr
    }
}