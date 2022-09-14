package com.aliyunm.weeeechat.adapter

import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aliyunm.weeeechat.ChatActivity
import com.aliyunm.weeeechat.R
import com.aliyunm.weeeechat.data.model.RoomModel

class RoomListAdapter(val data : ArrayList<RoomModel>) : RecyclerView.Adapter<RoomListAdapter.MessageViewHolder>() {

    lateinit var mContext : Context

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar : TextView = itemView.findViewById(R.id.avatar)
        val name : TextView = itemView.findViewById(R.id.name)
        val message : TextView = itemView.findViewById(R.id.message)
        val time : TextView = itemView.findViewById(R.id.time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        mContext = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_room, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val room = data[position]
        holder.avatar.text = room.name
        holder.name.text = room.name
        if (room.messages.size != 0) {
            val chat = room.messages.last()
            holder.message.text = "${chat.user.nickname}:${chat.content}"
            holder.time.text = timeFormat(chat.time)
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, ChatActivity::class.java)
            intent.putExtra("chats", room.messages)
            mContext.startActivity(intent)
        }
    }

    private fun timeFormat(time : Long): String {
        return DateUtils.formatDateTime(mContext, time, DateUtils.FORMAT_SHOW_TIME)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}