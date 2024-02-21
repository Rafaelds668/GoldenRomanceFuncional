package com.example.goldenromance.Matches

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.goldenromance.Chat.ChatActivity
import com.example.goldenromance.R

class MatchesViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    val mMatchId: TextView = itemView.findViewById(R.id.Matchid)
    val mMatchName: TextView = itemView.findViewById(R.id.MatchName)
    val mMatchImage: ImageView = itemView.findViewById(R.id.MatchImage)

    init {
        itemView.setOnClickListener(this)
    }
    override fun onClick(view: View?) {
       val intent = Intent(view!!.context, ChatActivity::class.java)
        val b = Bundle()
        b.putString("matchId", mMatchId.text.toString())
        intent.putExtras(b)
        view.context.startActivity(intent)
    }
}