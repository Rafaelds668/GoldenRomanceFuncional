package com.example.goldenromance.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.goldenromance.databinding.ItemUserLayoutBinding
import com.example.goldenromance.model.UserModel

class DatingAdapter(val content: Context, val list: List<UserModel>) : RecyclerView.Adapter<DatingAdapter.DatingViewHolder>() {
    inner class DatingViewHolder(val binding: ItemUserLayoutBinding)
        :RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatingViewHolder {


        return DatingViewHolder(ItemUserLayoutBinding.inflate(LayoutInflater.from(content)
       , parent, false))
    }

    override fun getItemCount(): Int {
       return list.size
    }

    override fun onBindViewHolder(holder: DatingViewHolder, position: Int) {

        holder.binding.textView5.text = list[position].name
        holder.binding.textView4.text = list[position].email

        Glide.with(content).load(list[position].image).into(holder.binding.userImage)

    }
}