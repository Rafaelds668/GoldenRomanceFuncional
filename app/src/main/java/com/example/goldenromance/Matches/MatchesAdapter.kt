package com.example.goldenromance.Matches

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.goldenromance.R

class MatchesAdapter(private val matchesList: List<MatchesObject>, private val context: Context) :
    RecyclerView.Adapter<MatchesViewHolders>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchesViewHolders {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.item_matches, parent, false)
        val lp = RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutView.layoutParams = lp
        return MatchesViewHolders(layoutView)
    }

    override fun getItemCount(): Int {
        return matchesList.size
    }


    override fun onBindViewHolder(holder: MatchesViewHolders, position: Int) {
        holder.mMatchId.text = matchesList[position].userId
        holder.mBudget.text = matchesList[position].budget
        holder.mGive.text = matchesList[position].give
        holder.mProfile.text = matchesList[position].profileImageUrl
        holder.mNeed.text = matchesList[position].need
        holder.mMatchName.text = matchesList[position].name
        holder.mLastMessage.text = matchesList[position].lastMessage

        val lastSeen = matchesList[position].lastSeen
        if (lastSeen == "true") {
            holder.mNotificationDot.visibility = View.VISIBLE
        } else {
            holder.mNotificationDot.visibility = View.GONE
        }

        holder.mLastTimeStamp.text = matchesList[position].lastTimeStamp

        if (matchesList[position].profileImageUrl != "default") {
            Glide.with(context).load(matchesList[position].profileImageUrl)
                .into(holder.mMatchImage)
        }
    }



}