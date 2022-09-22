package com.aliyunm.weeeechat.adapter

import android.view.Gravity.CENTER
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.aliyunm.weeeechat.data.model.Emoji

class EmojiAdapter(val callback : (String) -> Unit) : RecyclerView.Adapter<EmojiAdapter.EmojiItemViewHolder>() {

    class EmojiItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item : TextView = itemView as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiItemViewHolder {
        val view : TextView = TextView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        return EmojiItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmojiItemViewHolder, position: Int) {
        holder.apply {
            item.apply {
                textSize = 20f
                setPadding(10)
                gravity = CENTER
                text = Emoji.emoji[position]
                setOnClickListener {
                    callback(Emoji.emoji[position])
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return Emoji.emoji.size
    }
}