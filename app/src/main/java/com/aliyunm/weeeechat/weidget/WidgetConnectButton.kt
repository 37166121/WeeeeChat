package com.aliyunm.weeeechat.weidget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

class WidgetConnectButton : AppCompatButton {

    private val mContext : Context
    private val mAttrs : AttributeSet?
    private val mDefStyleAttr : Int

    constructor(context : Context) : this(context, null)

    constructor(context : Context, attrs : AttributeSet?) : this(context, attrs, 0)

    constructor(context : Context, attrs : AttributeSet?, defStyleAttr : Int) : super(context, attrs, defStyleAttr) {
        mContext = context
        mAttrs = attrs
        mDefStyleAttr = defStyleAttr
    }
}