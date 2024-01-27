package com.example.goldenromance.Matches

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.goldenromance.R
import com.example.goldenromance.chat.ChatActivity

class MatchesViewHolders (itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{



    var mMatchId: TextView = itemView.findViewById(R.id.Matchid)
    var mMatchName: TextView = itemView.findViewById(R.id.MatchName)
    var mLastMessage: TextView = itemView.findViewById(R.id.lastMessage)
    var mLastTimeStamp: TextView = itemView.findViewById(R.id.LastTimeStamp)
     var mNeed: TextView = itemView.findViewById(R.id.needid)
    var mGive: TextView = itemView.findViewById(R.id.giveid)
    var mBudget: TextView = itemView.findViewById(R.id.budgetid)
    var mMatchImage: ImageView = itemView.findViewById(R.id.MatchImage)
    var mProfile: TextView = itemView.findViewById(R.id.profileid)
    var mNotificationDot: ImageView = itemView.findViewById(R.id.notification_dot)
    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        val intent = Intent(view.context, ChatActivity::class.java)
        val b = Bundle()
        b.putString("matchId", mMatchId.text.toString())
        b.putString("matchNombre", mMatchName.text.toString())
        b.putString("UltimoMensaje", mLastMessage.text.toString())
        b.putString("UltimaVezConectado", mLastTimeStamp.text.toString())
        b.putString("budget", mBudget.text.toString())
        b.putString("necesidad", mNeed.text.toString())
        b.putString("dar", mGive.text.toString())
        b.putString("perfil", mProfile.text.toString())
        intent.putExtras(b)
        view.context.startActivity(intent)
    }


}