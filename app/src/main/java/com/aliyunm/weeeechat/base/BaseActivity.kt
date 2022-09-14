package com.aliyunm.weeeechat.base

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

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
        fullScreen()
    }

    abstract fun initData()

    abstract fun initView()

    abstract fun getViewBinding_(): VB

    abstract fun getViewModel_(): VM

    /**
     * 全屏
     */
    private fun fullScreen() {
        window.apply {
            setDecorFitsSystemWindows(false)
            statusBarColor = Color.TRANSPARENT
            navigationBarColor = Color.TRANSPARENT
        }
    }

    /**
     * 获取导航栏高度
     */
    fun getNavigationBarHeight(): Int {
        val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        val height: Int = resources.getDimensionPixelSize(resourceId)
        return height
    }

    /**
     * 获取状态栏高度
     */
    fun getStatusBarHeight(): Int {
        val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
        val height: Int = resources.getDimensionPixelSize(resourceId)
        return height
    }
}