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
    // Declaración de vistas en el ViewHolder
    val mMatchName: TextView = itemView.findViewById(R.id.MatchName) // TextView para el nombre del match
    val mMatchImage: ImageView = itemView.findViewById(R.id.MatchImage) // ImageView para la imagen del match

    // Inicialización del OnClickListener en el ViewHolder
    init {
        itemView.setOnClickListener(this) // Establece este ViewHolder como el OnClickListener del elemento de la lista
    }

    // Método onClick que se llama cuando se hace clic en el ViewHolder
    override fun onClick(view: View?) {
        // Crea un intent para abrir la actividad ChatActivity
        val intent = Intent(view!!.context, ChatActivity::class.java)
        // Crea un Bundle para pasar datos adicionales con el intent
        val b = Bundle()
        // Agrega el Bundle al intent
        intent.putExtras(b)
        // Inicia la actividad ChatActivity con el intent
        view.context.startActivity(intent)
    }
}