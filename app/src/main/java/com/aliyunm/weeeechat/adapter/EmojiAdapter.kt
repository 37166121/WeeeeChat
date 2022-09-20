package com.aliyunm.weeeechat.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aliyunm.weeeechat.data.model.Emoji
import com.aliyunm.weeeechat.util.ScaleUtil.dp2Px

class EmojiAdapter(val callback : (String) -> Unit) : Adapter<EmojiAdapter.EmojiViewHolder>() {

    private lateinit var context : Context

    class EmojiViewHolder(itemView: View) : ViewHolder(itemView) {
        val item : RecyclerView = itemView as RecyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        context = parent.context
        val view = RecyclerView(parent.context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }
        return EmojiViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        holder.apply {
            item.apply {
                layoutManager = GridLayoutManager(context, 7)
                adapter = EmojiItemAdapter(Emoji.emoji[position]) {
                    callback(it)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return Emoji.emoji.size
    }
}