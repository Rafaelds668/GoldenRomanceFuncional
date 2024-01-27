package com.example.goldenromance.chat

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.goldenromance.R

class ChatAdapter (private val chatList: List<ChatObject>, private val context: Context) :
    RecyclerView.Adapter<ChatViewHolderers>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolderers {
        val layoutView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        val lp = RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        layoutView.layoutParams(lp)
        return ChatViewHolderers(layoutView)
    }

    override fun onBindViewHolder(holder: ChatViewHolderers, position: Int) {
        holder.mMessage.text = chatList[position].message

        val shape = GradientDrawable()
        shape.cornerRadius = 20f
        shape.cornerRadii = floatArrayOf(25f, 25f, 3f, 25f, 25f, 25f, 25f, 25f)

        if (chatList[position].currentUser) {
            holder.mContainer.gravity = Gravity.END
            shape.setColor(Color.parseColor("#53d769"))
        } else {
            holder.mContainer.gravity = Gravity.START
            shape.setColor(Color.parseColor("#FFFFFF"))
        }

        holder.mMessage.background = shape
        holder.mMessage.setTextColor(Color.parseColor("#FFFFFF"))
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

}