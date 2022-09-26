package com.aliyunm.weeeechat.activity

import android.view.KeyEvent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.aliyunm.weeeechat.R
import com.aliyunm.weeeechat.adapter.ChatListAdapter
import com.aliyunm.weeeechat.adapter.EmojiAdapter
import com.aliyunm.weeeechat.base.BaseActivity
import com.aliyunm.weeeechat.data.model.ChatModel
import com.aliyunm.weeeechat.data.model.MessageModel
import com.aliyunm.weeeechat.databinding.ActivityChatBinding
import com.aliyunm.weeeechat.network.socket.SocketManage
import com.aliyunm.weeeechat.network.socket.SocketManage.addChat
import com.aliyunm.weeeechat.util.KeyboardUtil
import com.aliyunm.weeeechat.util.ScreenUtil.getNavigationBarHeight
import com.aliyunm.weeeechat.util.ScreenUtil.getStatusBarHeight
import com.aliyunm.weeeechat.viewmodel.ChatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatActivity : BaseActivity<ActivityChatBinding, ChatViewModel>() {
    private lateinit var chatListAdapter : ChatListAdapter
    private lateinit var emojiAdapter : EmojiAdapter

    override fun initData() {

        viewModel.apply {

            intent.apply {
                rid = getIntExtra("rid", 0)
                uid = getStringExtra("uid") ?: ""
                room = SocketManage.getRoom(rid)
            }

            chats.addAll(room.messages)

            onMessage(this@ChatActivity) {
                if (it.rid == rid) {
                    chats.add(it)
                    CoroutineScope(Dispatchers.Main).launch {
                        chatListAdapter.notifyItemInserted(chats.size - 1)
                        viewBinding.chatList.scrollToPosition(chats.size - 1)
                    }
                }
            }
        }
        chatListAdapter = ChatListAdapter(viewModel.chats)
        emojiAdapter = EmojiAdapter {
            viewBinding.chatEnter.apply {
                setText("${text}${it}")
                setSelection(text.length)
            }
        }
        SocketManage.roomMessage.observe(this) {
            SocketManage.roomManager.forEach {
                if (it.rid == viewModel.room.rid) {
                    viewBinding.roomQuantity.text = "${it.count}人在线"
                }
            }
        }
    }

    override fun initView() {

        viewBinding.apply {

            viewmodel = viewModel

            // 广场和私聊不显示人数
            if (viewModel.rid == 0) {
                roomQuantity.visibility = View.GONE
            } else {
                roomQuantity.text = "${viewModel.room.count}人在线"
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
                text = viewModel.room.name
            }

            // toolbar.apply {
            //     post {
            //         minimumHeight += getStatusBarHeight(this@ChatActivity)
            //     }
            //     setPadding(paddingStart, paddingTop + getStatusBarHeight(this@ChatActivity), paddingEnd, paddingBottom)
            // }
            //
            // navbar.apply {
            //     post {
            //         minimumHeight += getNavigationBarHeight(this@ChatActivity)
            //     }
            //     setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom + getNavigationBarHeight(this@ChatActivity))
            // }

            chatList.apply {
                layoutManager = LinearLayoutManager(this@ChatActivity)
                adapter = this@ChatActivity.chatListAdapter
                itemAnimator = null
                scrollToPosition(viewModel.chats.size - 1)
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
                layoutManager = GridLayoutManager(context, 7)
                adapter = emojiAdapter
            }
        }
    }

    private fun send() {
        viewModel.apply {
            if ((chatEnter.value ?: "").isNotBlank()) {
                val message = "${chatEnter.value}"
                val chatModel = ChatModel(content = message, rid = rid, uid = uid, type = if (uid != "") { MessageModel.PRIVATE } else { MessageModel.SPECIFY })
                sendMessage(chatModel.type, chatModel) {
                    if (it) {
                        SocketManage.chatManager.addChat(chatModel)
                    }
                }
            }
        }
    }

    override fun isFull(): Boolean {
        return false
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
            send()
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