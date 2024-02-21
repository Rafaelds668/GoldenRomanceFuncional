package com.example.goldenromance.Cards

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide
import com.example.goldenromance.R


class cardsArrayAdapter<T>(context: Context, resourceId: Int, items: List<cards>) : ArrayAdapter<cards>(context, resourceId, items){

    override fun getView(position: Int, convertView: View?,parent: ViewGroup) : View{
        var convertViewVar = convertView
        val cardItem = getItem(position)

        if (convertViewVar == null){
            convertViewVar = LayoutInflater.from(context).inflate(R.layout.item, parent, false)
        }

        val name = convertViewVar!!.findViewById<TextView>(R.id.name)
        val image = convertViewVar.findViewById<ImageView>(R.id.image)

        name.text = cardItem!!.name
        when(cardItem!!.profileImageUrl){
            "default" -> Glide.with(convertViewVar.context).load(R.mipmap.ic_launcher).into(image)
            else ->{
                Glide.with(convertViewVar.context).clear(image)
                Glide.with(convertViewVar.context).load(cardItem!!.profileImageUrl).into(image)
            }
        }

        return convertViewVar
    }
}
