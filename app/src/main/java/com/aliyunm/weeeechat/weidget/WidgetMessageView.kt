package com.aliyunm.weeeechat.weidget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.setPadding
import com.aliyunm.weeeechat.R
import kotlinx.coroutines.*

class WidgetMessageView : AppCompatEditText {

    private val mContext : Context
    private val mAttrs : AttributeSet?
    private val mDefStyleAttr : Int

    constructor(context : Context) : this(context, null)

    constructor(context : Context, attrs : AttributeSet?) : this(context, attrs, 0)

    constructor(context : Context, attrs : AttributeSet?, defStyleAttr : Int) : super(context, attrs, defStyleAttr) {
        mContext = context
        mAttrs = attrs
        mDefStyleAttr = defStyleAttr
        isEnabled = false
        background = mContext.getDrawable(R.drawable.style_chat_bg)
        setPadding(30)
    }

    // override fun onDraw(canvas: Canvas?) {
    //     super.onDraw(canvas)
    // }
    //
    // override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    //     super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    // }

    /**
     * 画自己的消息
     * <br>
     * 靠左
     */
    private fun drawSelf() {

    }

    /**
     * 画别人的消息
     * <br>
     * 靠右
     */
    private fun drawOther() {

    }

    private lateinit var job : Job

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action) {

            MotionEvent.ACTION_DOWN     -> {
                job = CoroutineScope(Dispatchers.IO).launch {
                    delay(500)
                    // 显示touchBar
                    launch(Dispatchers.Main) {
                        Toast.makeText(mContext, "显示touchBar", Toast.LENGTH_SHORT).show()
                    }
                }
                job.start()
            }

            MotionEvent.ACTION_MOVE     -> {
                job.cancel()
            }

            MotionEvent.ACTION_UP       -> {
                job.cancel()
            }

            MotionEvent.ACTION_CANCEL   -> {
                job.cancel()
            }
        }
        return true
    }
}