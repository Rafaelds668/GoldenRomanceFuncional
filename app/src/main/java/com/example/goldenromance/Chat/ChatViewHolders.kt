package com.example.goldenromance.Chat

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.goldenromance.R

class ChatViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    var mMessage: TextView
    var mContainer: LinearLayout

    init {
        itemView.setOnClickListener(this)
        mMessage = itemView.findViewById(R.id.message)
        mContainer = itemView.findViewById(R.id.container)
    }

    override fun onClick(view: View) {
        // Código para manejar clics en el elemento de la vista, si es necesario.
    }
}