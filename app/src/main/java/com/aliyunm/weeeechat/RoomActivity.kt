package com.aliyunm.weeeechat

import android.content.Intent
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aliyunm.weeeechat.adapter.RoomListAdapter
import com.aliyunm.weeeechat.base.BaseActivity
import com.aliyunm.weeeechat.data.model.ChatModel
import com.aliyunm.weeeechat.data.model.MessageModel
import com.aliyunm.weeeechat.data.model.RoomModel
import com.aliyunm.weeeechat.databinding.ActivityRoomBinding
import com.aliyunm.weeeechat.network.socket.SocketManage
import com.aliyunm.weeeechat.util.ScreenUtil.getNavigationBarHeight
import com.aliyunm.weeeechat.util.ScreenUtil.getStatusBarHeight
import com.aliyunm.weeeechat.viewmodel.RoomViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RoomActivity : BaseActivity<ActivityRoomBinding, RoomViewModel>() {

    private val rooms : ArrayList<RoomModel> = SocketManage.roomManager
    private lateinit var adapter : RoomListAdapter

    override fun initData() {
        adapter = RoomListAdapter(rooms)
        viewModel.onMessage(this) {
            rooms.forEachIndexed { index, roomModel ->
                if (roomModel.rid == it.rid) {
                    CoroutineScope(Dispatchers.Main).launch {
                        adapter.notifyItemChanged(index)
                    }
                }
            }
        }
        SocketManage._roomManager.observe(this) {
            if (it) {
                CoroutineScope(Dispatchers.Main).launch {
                    adapter.notifyItemInserted(rooms.size - 1)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.rooms.forEach { room ->
            room.messages.clear()
            SocketManage.chatManager.forEach {
                if (room.rid == it.rid) {
                    room.messages.add(it)
                }
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            adapter.notifyDataSetChanged()
        }
    }

    override fun initView() {

        viewBinding.apply {
            viewmodel = viewModel

            appbar.apply {
                (layoutParams as CoordinatorLayout.LayoutParams).apply {
                    setMargins(marginStart, marginTop + getStatusBarHeight(this@RoomActivity), marginEnd, marginBottom)
                }
            }

            roomList.apply {
                adapter = this@RoomActivity.adapter
                layoutManager = LinearLayoutManager(this@RoomActivity)
                itemAnimator = null
                setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom + getStatusBarHeight(this@RoomActivity) + getNavigationBarHeight(this@RoomActivity))
            }

            menu.setOnClickListener {
                showPopupWindow(it)
            }
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            viewBinding.search.apply {
                // rooms.add(RoomModel(rid = text.toString().toInt(), name = text.toString()))
                viewModel.enterRoom(ChatModel(type = MessageModel.ENTER_ROOM, rid = text.toString().toInt())) {}
                setText("")
                clearFocus()
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    lateinit var popup : PopupWindow

    private fun showPopupWindow(parent : View) {
        val view = LayoutInflater.from(this@RoomActivity).inflate(R.layout.menu_room, null)
        view.findViewById<TextView>(R.id.logout).apply {
            setOnClickListener {
                SocketManage.onClose {
                    val intent = Intent(this@RoomActivity, MainActivity::class.java)
                    intent.putExtra("isLogout", true)
                    startActivity(intent)
                    finish()
                }
            }
        }

        popup = PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true).apply {
            isTouchable = true
            showAsDropDown(parent, -100, 20)
        }
    }

    /**
     * 背景变暗
     */
    // private fun darkenBackground(bgcolor: Float) {
    //     window.apply {
    //         attributes.apply {
    //             alpha = bgcolor
    //         }
    //         addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    //     }
    // }

    override fun getViewBinding_(): ActivityRoomBinding {
        return ActivityRoomBinding.inflate(layoutInflater)
    }

    override fun getViewModel_(): RoomViewModel {
        return ViewModelProvider(this).get(RoomViewModel::class.java)
    }

    override fun onStop() {
        super.onStop()
        if (this::popup.isInitialized) {
            popup.dismiss()
        }
    }
}