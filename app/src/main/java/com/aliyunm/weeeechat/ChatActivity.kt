package com.aliyunm.weeeechat

import android.view.KeyEvent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aliyunm.weeeechat.adapter.ChatListAdapter
import com.aliyunm.weeeechat.adapter.EmojiAdapter
import com.aliyunm.weeeechat.base.BaseActivity
import com.aliyunm.weeeechat.data.model.ChatModel
import com.aliyunm.weeeechat.data.model.MessageModel
import com.aliyunm.weeeechat.data.model.RoomModel
import com.aliyunm.weeeechat.databinding.ActivityChatBinding
import com.aliyunm.weeeechat.network.socket.SocketManage
import com.aliyunm.weeeechat.network.socket.SocketManage.addNotice
import com.aliyunm.weeeechat.util.KeyboardUtil
import com.aliyunm.weeeechat.util.KeyboardUtil.getKeyboardHeight
import com.aliyunm.weeeechat.util.ScreenUtil.getNavigationBarHeight
import com.aliyunm.weeeechat.util.ScreenUtil.getStatusBarHeight
import com.aliyunm.weeeechat.viewmodel.ChatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatActivity : BaseActivity<ActivityChatBinding, ChatViewModel>() {
    private lateinit var room : RoomModel
    private val chats : ArrayList<ChatModel> = arrayListOf()
    private lateinit var chatListAdapter : ChatListAdapter
    private lateinit var emojiAdapter : EmojiAdapter

    override fun initData() {
        getKeyboardHeight(this)
        chats.addAll(SocketManage.roomChats)
        chatListAdapter = ChatListAdapter(chats)
        emojiAdapter = EmojiAdapter {
            viewBinding.apply {
                chatEnter.setText("${chatEnter.text}${it}")
                chatEnter.setSelection(chatEnter.text.length)
            }
        }

        SocketManage._roomManager.observe(this) {
            SocketManage.roomManager.forEach {
                if (it.rid == room.rid) {
                    viewBinding.roomQuantity.text = "${it.count}人在线"
                }
            }
        }

        viewModel.apply {

            intent.apply {
                rid = getIntExtra("rid", 0)
                uid = getStringExtra("uid") ?: ""
                SocketManage.roomManager.forEach {
                    if (it.rid == rid) {
                        room = it
                    }
                }
            }

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
                    chatListAdapter.notifyItemInserted(chats.size - 1)
                    viewBinding.chatList.scrollToPosition(chats.size - 1)
                }
            }
        }
    }

    override fun initView() {

        viewBinding.apply {

            viewmodel = viewModel

            // 大厅和私聊不显示人数
            if (viewModel.rid == 0) {
                roomQuantity.visibility = View.GONE
            } else {
                roomQuantity.text = "${room.count}人在线"
            }

            viewModel.chatEnter.observe(this@ChatActivity) {
                send.setBackgroundResource(if (it.isBlank()) {
                    R.drawable.style_send_null_bg
                } else {
                    R.drawable.style_send_bg
                })
            }

            back.apply {
                setOnClickListener {
                    finish()
                }
            }

            roomName.apply {
                text = room.name
            }

            toolbar.apply {
                post {
                    minimumHeight += getStatusBarHeight(this@ChatActivity)
                }
                setPadding(paddingStart, paddingTop + getStatusBarHeight(this@ChatActivity), paddingEnd, paddingBottom)
            }

            chatList.apply {
                layoutManager = LinearLayoutManager(this@ChatActivity)
                adapter = this@ChatActivity.chatListAdapter
                itemAnimator = null
                scrollToPosition(chats.size - 1)
            }

            navbar.apply {
                post {
                    minimumHeight += getNavigationBarHeight(this@ChatActivity)
                }
                setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom + getNavigationBarHeight(this@ChatActivity))
            }

            emoji.apply {

                var isShow = false

                setOnClickListener {
                    viewBinding.emojiList.visibility = if (isShow) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
                    isShow = !isShow
                }
            }

            send.apply {
                setOnClickListener {
                    send()
                }
            }

            emojiList.apply {
                post {
                    minimumHeight = KeyboardUtil.keyboardHeight
                }
                layoutManager = LinearLayoutManager(this@ChatActivity)
                adapter = emojiAdapter
            }
        }
    }

    private fun send() {
        viewModel.apply {
            if ((chatEnter.value ?: "").isNotBlank()) {
                val message = "${chatEnter.value}"
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
                        SocketManage.chatManager.addNotice(chatModel)
                    }
                }
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