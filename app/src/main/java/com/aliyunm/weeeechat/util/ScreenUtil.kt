package com.aliyunm.weeeechat.util

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.view.Window
import android.view.WindowManager
import com.blankj.utilcode.util.Utils

object ScreenUtil {

    /**
     * 全屏
     */
    fun fullScreen(window : Window) {
        window.apply {
            setDecorFitsSystemWindows(false)
            statusBarColor = Color.TRANSPARENT
            navigationBarColor = Color.TRANSPARENT
        }
    }

    /**
     * 获取导航栏高度
     */
    fun getNavigationBarHeight(context: Context): Int {
        val resourceId: Int = context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        val height: Int = context.resources.getDimensionPixelSize(resourceId)
        return height
    }

    /**
     * 获取状态栏高度
     */
    fun getStatusBarHeight(context: Context): Int {
        val resourceId: Int = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        val height: Int = context.resources.getDimensionPixelSize(resourceId)
        return height
    }

    /**
     * 获取屏幕宽度
     */
    fun getScreenWidth(): Int {
        val wm = Utils.getApp().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        wm.defaultDisplay.getRealSize(point)
        return point.x
    }

    /**
     * 获取屏幕高度
     */
    fun getScreenHeight(): Int {
        val wm = Utils.getApp().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        wm.defaultDisplay.getRealSize(point)
        return point.y
    }
}