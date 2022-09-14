package com.aliyunm.weeeechat

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aliyunm.weeeechat.adapter.ChatListAdapter
import com.aliyunm.weeeechat.base.BaseActivity
import com.aliyunm.weeeechat.data.model.ChatModel
import com.aliyunm.weeeechat.data.model.MessageModel
import com.aliyunm.weeeechat.databinding.ActivityChatBinding
import com.aliyunm.weeeechat.network.socket.SocketManage
import com.aliyunm.weeeechat.viewmodel.ChatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatActivity : BaseActivity<ActivityChatBinding, ChatViewModel>() {

    private val chats : ArrayList<ChatModel> = arrayListOf()
    private lateinit var adapter : ChatListAdapter

    override fun initData() {
        adapter = ChatListAdapter(chats)
        chats.addAll(SocketManage.chats)
        viewModel.onMessage(this) {
            chats.add(it)
            CoroutineScope(Dispatchers.Main).launch {
                adapter.notifyItemInserted(chats.size - 1)
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
                val navigationBarHeight = getNavigationBarHeight()
                val statusBarHeight = getStatusBarHeight()
                setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom + navigationBarHeight + statusBarHeight)
            }

            navbar.apply {
                post {
                    minimumHeight += getNavigationBarHeight()
                }
                setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom + getNavigationBarHeight())
            }

            send.apply {
                setOnClickListener {
                    val message = viewModel.chatEnter.value ?: ""
                    viewModel.sendMessage(MessageModel.BROADCAST, message)
                    val chat = ChatModel(message)
                    chats.add(chat)
                    SocketManage.chats.add(chat)
                    adapter.notifyItemInserted(chats.size - 1)
                    chatList.scrollToPosition(chats.size - 1)
                }
            }
        }

    }

    override fun getViewBinding_(): ActivityChatBinding {
        return ActivityChatBinding.inflate(layoutInflater)
    }

    override fun getViewModel_(): ChatViewModel {
        return ViewModelProvider(this).get(ChatViewModel::class.java)
    }
}