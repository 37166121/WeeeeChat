package com.aliyunm.weeeechat.adapter

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aliyunm.weeeechat.R
import com.aliyunm.weeeechat.data.model.ChatModel
import com.aliyunm.weeeechat.data.model.MessageModel
import com.aliyunm.weeeechat.util.SharedPreferencesUtil

class ChatListAdapter(val data : ArrayList<ChatModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var mContext : Context

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val other : LinearLayout = itemView.findViewById(R.id.other)
        val name_other : TextView = itemView.findViewById(R.id.name_other)
        val message_other : EditText = itemView.findViewById(R.id.message_other)
        val time_other : TextView = itemView.findViewById(R.id.time_other)

        val self : LinearLayout = itemView.findViewById(R.id.self)
        val name_self : TextView = itemView.findViewById(R.id.name_self)
        val message_self : EditText = itemView.findViewById(R.id.message_self)
        val time_self : TextView = itemView.findViewById(R.id.time_self)
    }

    class EnterRoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val enter_room : TextView = itemView.findViewById(R.id.enter_room)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = parent.context
        when(viewType) {

            MessageModel.ENTER_ROOM -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_enter_room, parent, false)
                return EnterRoomViewHolder(view)
            }

            MessageModel.QUIT_ROOM  -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_enter_room, parent, false)
                return EnterRoomViewHolder(view)
            }

            else                    -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
                return ChatViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chat = data[position]
        val uid = SharedPreferencesUtil.getString("uid", "")
        when(holder) {

            is ChatViewHolder -> {
                if (chat.fromUid == uid) {
                    holder.apply {
                        self.visibility = View.VISIBLE
                        other.visibility = View.GONE
                        name_self.text = chat.nickname
                        message_self.setText(chat.content)
                        time_self.text = timeFormat(chat.time)
                    }
                } else {
                    holder.apply {
                        self.visibility = View.GONE
                        other.visibility = View.VISIBLE
                        name_other.text = chat.nickname
                        message_other.setText(chat.content)
                        time_other.text = timeFormat(chat.time)
                    }
                }
            }

            is EnterRoomViewHolder -> {
                holder.enter_room.text = "${chat.nickname}${if (chat.type == MessageModel.ENTER_ROOM) "进入" else "退出"}房间"
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].type
    }

    private fun timeFormat(time : Long): String {
        return DateUtils.formatDateTime(mContext, time, DateUtils.FORMAT_SHOW_TIME)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}