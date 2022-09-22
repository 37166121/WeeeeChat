package com.aliyunm.weeeechat

import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.aliyunm.weeeechat.base.BaseActivity
import com.aliyunm.weeeechat.data.model.UserModel
import com.aliyunm.weeeechat.data.repository.DatabaseHelper
import com.aliyunm.weeeechat.databinding.ActivityMainBinding
import com.aliyunm.weeeechat.network.socket.SocketManage
import com.aliyunm.weeeechat.network.socket.SocketManage.default
import com.aliyunm.weeeechat.util.RSAUtil
import com.aliyunm.weeeechat.util.SharedPreferencesUtil
import com.aliyunm.weeeechat.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    override fun initData() {
        val isLogout = intent.getBooleanExtra("isLogout", false)
        if (isLogout) {
            clear()
        } else {
            val uid = SharedPreferencesUtil.getString("uid", "")
            val nickname = SharedPreferencesUtil.getString("nickname", "")
            if (uid.isNotBlank() && nickname.isNotBlank()) {
                login(uid, nickname)
            }
        }
    }

    fun clear() {
        CoroutineScope(Dispatchers.IO).launch {
            SharedPreferencesUtil.remove("uid", "nickname")
            DatabaseHelper.deleteAll()
            SocketManage.roomManager.default()
            SocketManage.chatManager.clear()
        }
    }

    override fun initView() {
        viewBinding.apply {
            viewmodel = viewModel

            nickname.apply {
                setHintTextColor(this@MainActivity.getColor(R.color.white))
            }

            connect.apply {
                setOnClickListener {
                    login(UUID.randomUUID().toString(), viewModel.nickname.value ?: "")
                }
            }
        }
    }

    private fun login(uid : String, nickname : String) {
        if (SocketManage.publicKey.isNotBlank()) {
            if (uid.isNotBlank() && nickname.isNotBlank()) {
                viewModel.login(UserModel(uid, nickname, RSAUtil.getPublicToBase64String())) {
                    next(it)
                }
            } else {
                Toast.makeText(this, "昵称不能为空", Toast.LENGTH_SHORT).show()
            }
        } else {
            viewModel.getPublicKey {
                login(uid, nickname)
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