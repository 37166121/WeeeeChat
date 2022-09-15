package com.aliyunm.weeeechat

import android.view.KeyEvent
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aliyunm.weeeechat.adapter.ChatListAdapter
import com.aliyunm.weeeechat.base.BaseActivity
import com.aliyunm.weeeechat.data.model.ChatModel
import com.aliyunm.weeeechat.data.model.MessageModel
import com.aliyunm.weeeechat.databinding.ActivityChatBinding
import com.aliyunm.weeeechat.network.socket.SocketManage
import com.aliyunm.weeeechat.network.socket.SocketManage.addNotice
import com.aliyunm.weeeechat.viewmodel.ChatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatActivity : BaseActivity<ActivityChatBinding, ChatViewModel>() {

    private val chats : ArrayList<ChatModel> = arrayListOf()
    private lateinit var adapter : ChatListAdapter

    override fun initData() {
        adapter = ChatListAdapter(chats)
        chats.addAll(SocketManage.room_chats)
        viewModel.apply {
            rid = intent.getIntExtra("rid", 0)
            uid = intent.getStringExtra("uid") ?: ""
            name = intent.getStringExtra("name") ?: ""
            onMessage(this@ChatActivity) {
                if (uid != "") {
                    // 私聊 比较toUid是否相同
                    if (it.uid == uid) {
                        chats.add(it)
                    }
                } else {
                    // 群聊 比较toRid是否相同
                    if (it.rid == rid) {
                        chats.add(it)
                    }
                }
                CoroutineScope(Dispatchers.Main).launch {
                    adapter.notifyItemInserted(chats.size - 1)
                    viewBinding.chatList.scrollToPosition(chats.size - 1)
                }
            }
        }
    }

    override fun initView() {

        viewBinding.apply {

            viewModel.chatEnter.observe(this@ChatActivity) {
                send.setBackgroundResource(if (it.isBlank()) {
                    R.drawable.style_send_null_bg
                } else {
                    R.drawable.style_send_bg
                })
            }

            viewmodel = viewModel

            back.apply {
                setOnClickListener {
                    finish()
                }
            }

            roomName.text = viewModel.name

            toolbar.apply {
                post {
                    minimumHeight += getStatusBarHeight()
                }
                setPadding(paddingStart, paddingTop + getStatusBarHeight(), paddingEnd, paddingBottom)
            }

            chatList.apply {
                layoutManager = LinearLayoutManager(this@ChatActivity)
                adapter = this@ChatActivity.adapter
                itemAnimator = null
                scrollToPosition(chats.size - 1)
            }

            navbar.apply {
                post {
                    minimumHeight += getNavigationBarHeight()
                }
                setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom + getNavigationBarHeight())
            }

            send.apply {
                setOnClickListener {
                    send()
                }
            }
        }
    }

    private fun send() {
        viewModel.apply {
            if ((chatEnter.value ?: "").isNotBlank()) {
                val message = chatEnter.value ?: ""
                val type: Int
                val chatModel = if (uid != "") {
                    type = MessageModel.PRIVATE
                    ChatModel(content = message, uid = uid)
                } else {
                    type = MessageModel.SPECIFY
                    ChatModel(content = message, rid = rid)
                }
                sendMessage(type, chatModel) {
                    if (it) {
                        chatEnter.postValue("")
                    }
                }
                SocketManage.chats.addNotice(chatModel)
            }
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                send()
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun getViewBinding_(): ActivityChatBinding {
        return ActivityChatBinding.inflate(layoutInflater)
    }

    override fun getViewModel_(): ChatViewModel {
        return ViewModelProvider(this).get(ChatViewModel::class.java)
    }
}