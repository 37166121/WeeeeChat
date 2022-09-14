package com.aliyunm.weeeechat.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding, VM : ViewModel> : Fragment() {

    private lateinit var binding : VB
    private lateinit var viewmodel : VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = getBinding()
        viewmodel = getViewModel()
        initData()
        initView()
        return binding.root
    }

    abstract fun initData()

    abstract fun initView()

    abstract fun getBinding(): VB

    abstract fun getViewModel(): VM
}