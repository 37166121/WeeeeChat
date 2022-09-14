package com.aliyunm.weeeechat

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import com.aliyunm.weeeechat.base.BaseActivity
import com.aliyunm.weeeechat.data.model.UserModel
import com.aliyunm.weeeechat.databinding.ActivityMainBinding
import com.aliyunm.weeeechat.util.RSAUtil
import com.aliyunm.weeeechat.util.SharedPreferencesUtil
import com.aliyunm.weeeechat.viewmodel.MainViewModel
import java.util.UUID

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    override fun initData() {
        val isLogout = intent.getBooleanExtra("isLogout", false)
        if (isLogout) {
            SharedPreferencesUtil.remove("uid", "nickname")
        } else {
            viewModel.getPublicKey {
                val uid = SharedPreferencesUtil.getString("uid", "")
                val nickname = SharedPreferencesUtil.getString("nickname", "")
                if (uid.isNotBlank() && nickname.isNotBlank()) {
                    viewModel.login(UserModel(uid, nickname, RSAUtil.getPublicToBase64String())) {
                        next(it)
                    }
                }
            }
        }
    }

    override fun initView() {
        viewBinding.let { it ->
            it.viewmodel = viewModel

            it.nickname.apply {
                setHintTextColor(this@MainActivity.getColor(R.color.white))
            }

            it.connect.apply {
                setOnClickListener {
                    viewModel.login(UserModel(UUID.randomUUID().toString(), viewModel.nickname.value ?: "", RSAUtil.getPublicToBase64String())) {
                        next(it)
                    }
                }
            }
        }
    }

    private fun next(connect : Boolean) {
        if (connect) {
            val intent = Intent(this@MainActivity, RoomActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            viewBinding.connect.apply {
                setBackgroundResource(R.drawable.style_connect_failed_bg)
                text = "重试"
            }
        }
    }

    override fun getViewBinding_(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun getViewModel_(): MainViewModel {
        return ViewModelProvider(this).get(MainViewModel::class.java)
    }
}