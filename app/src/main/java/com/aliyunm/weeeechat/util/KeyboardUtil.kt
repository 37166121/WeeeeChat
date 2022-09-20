package com.aliyunm.weeeechat.util

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.Utils

object KeyboardUtil {

    var keyboardHeight = 0

    fun getKeyboardHeight(activity: Activity) {
        if (keyboardHeight == 0) {
            val view = activity.findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
            getKeyboardHeight(activity) {
            }
        }
    }

    fun getKeyboardHeight(activity: Activity, callback : () -> Unit = {}) {
        val decorView: View = activity.window.decorView
        decorView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            decorView.getWindowVisibleDisplayFrame(rect)
            // 计算出可见屏幕的高度
            val displayHeight: Int = rect.bottom - rect.top
            // 获得屏幕整体的高度
            val height: Int = decorView.height
            // 获得键盘高度
            keyboardHeight = height - displayHeight
            callback()
        }
    }

    /**
     * Show the soft input.
     *
     * @param view The view.
     */
    fun showSoftInput(view: View) {
        showSoftInput(view, 0)
    }

    /**
     * Show the soft input.
     *
     * @param view  The view.
     * @param flags Provides additional operating flags.  Currently may be
     * 0 or have the [InputMethodManager.SHOW_IMPLICIT] bit set.
     */
    fun showSoftInput(view: View, flags: Int) {
        val imm = Utils.getApp().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view.isFocusable = true
        view.isFocusableInTouchMode = true
        view.requestFocus()
        imm.showSoftInput(view, flags, object : ResultReceiver(Handler()) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
                if (resultCode == InputMethodManager.RESULT_UNCHANGED_HIDDEN
                    || resultCode == InputMethodManager.RESULT_HIDDEN
                ) {
                    KeyboardUtils.toggleSoftInput()
                }
            }
        })
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    /**
     * Hide the soft input.
     *
     * @param activity The activity.
     */
    fun hideSoftInput(activity: Activity) {
        hideSoftInput(activity.window)
    }

    /**
     * Hide the soft input.
     *
     * @param window The window.
     */
    fun hideSoftInput(window: Window) {
        var view = window.currentFocus
        if (view == null) {
            val decorView = window.decorView
            val focusView = decorView.findViewWithTag<View>("keyboardTagView")
            if (focusView == null) {
                view = EditText(window.context)
                view.setTag("keyboardTagView")
                (decorView as ViewGroup).addView(view, 0, 0)
            } else {
                view = focusView
            }
            view.requestFocus()
        }
        KeyboardUtils.hideSoftInput(view)
    }
}