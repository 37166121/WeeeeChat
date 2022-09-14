package com.aliyunm.weeeechat.weidget

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.aliyunm.weeeechat.util.BitmapUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WidgetDispersionViewGroup : ConstraintLayout {

    private var mContext: Context
    private var mAttrs : AttributeSet?
    private var mDefStyleAttr : Int
    private var mDefStyleRes : Int

    /**
     * 模糊程度
     */
    private val blurRadius: Float = 170f

    private val views : MutableList<WidgetDispersionView> = mutableListOf()

    constructor(context : Context) : this(context, null)

    constructor(context : Context, attrs : AttributeSet?) : this(context, attrs, 0)

    constructor(context : Context, attrs : AttributeSet?, defStyleAttr : Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context : Context, attrs : AttributeSet?, defStyleAttr : Int, defStyleRes : Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        mContext = context
        mAttrs = attrs
        mDefStyleAttr = defStyleAttr
        mDefStyleRes = defStyleRes
        init()
    }

    private fun init() {
        // 每5秒内生成一个 WidgetView
        CoroutineScope(Dispatchers.Main).launch {
            for (i in 0 until 20) {
                delay(time())
                val view = WidgetDispersionView(mContext)
                views.add(view)
                addView(view)
            }
        }
    }

    private fun time(): Long {
        return (Math.random() * 5L).toLong()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        gaussian()
        super.onLayout(changed, left, top, right, bottom)
    }

    /**
     * 高斯模糊
     */
    private fun gaussian() {
        BitmapUtil.blur(this, blurRadius)
    }

    /**
     * 噪点
     */
    fun noise() {

    }
}