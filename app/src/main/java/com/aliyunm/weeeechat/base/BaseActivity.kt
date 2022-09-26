package com.aliyunm.weeeechat.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.aliyunm.weeeechat.util.ScreenUtil.fullScreen
import com.blankj.utilcode.util.KeyboardUtils

abstract class BaseActivity<VB : ViewBinding, VM : ViewModel> : AppCompatActivity() {

    lateinit var viewBinding : VB
    lateinit var viewModel : VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = getViewBinding_()
        setContentView(viewBinding.root)
        viewModel = getViewModel_()
        initData()
        initView()
        if (isFull()) {
            fullScreen(window)
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    abstract fun initData()

    abstract fun initView()

    abstract fun getViewBinding_(): VB

    abstract fun getViewModel_(): VM

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v != null) {
                if (isShouldHideKeyboard(v, ev)) {
                    KeyboardUtils.hideSoftInput(this)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    open fun isFull() : Boolean {
        return true
    }

    // Return whether touch the view.
    private fun isShouldHideKeyboard(v : View, event : MotionEvent): Boolean {
        if ((v is EditText)) {
            val l = IntArray(2)
            v.getLocationOnScreen(l)
            val left : Int = l[0]
            val top : Int = l[1]
            val bottom : Int = top + v.getHeight()
            val right : Int = left + v.getWidth()
            return !(event.rawX > left && event.rawX < right
                    && event.rawY > top && event.rawY < bottom)
        }
        return false
    }
}