package com.example.goldenromance.chat

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.goldenromance.R

class ChatViewHolderers(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    val mMessage: TextView = itemView.findViewById(R.id.message)
    val mContainer: LinearLayout = itemView.findViewById(com.google.android.material.R.id.container)

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        // Handle click event if needed
    }
}
