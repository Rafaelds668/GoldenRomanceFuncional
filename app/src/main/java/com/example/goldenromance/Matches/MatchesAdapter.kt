package com.example.goldenromance.Matches

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.goldenromance.R

class MatchesAdapter (private val matchesList: List<MatchesObject>, private val context: Context): RecyclerView.Adapter<MatchesViewHolders>() {
    // Método llamado cuando se necesita crear un nuevo ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchesViewHolders {
        // Infla el diseño de la vista de elemento de la lista
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.item_matches, parent, false)
        val lp = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutView.layoutParams = lp
        return MatchesViewHolders(layoutView)
    }

    // Método que devuelve el número de elementos en la lista
    override fun getItemCount(): Int {
        return matchesList.size
    }

    // Método llamado cuando se enlaza un ViewHolder a datos específicos
    override fun onBindViewHolder(holder: MatchesViewHolders, position: Int) {
        // Obtiene el objeto MatchesObject en la posición dada
        val match = matchesList[position]

        // Establece el ID y el nombre del match en los TextView correspondientes
        holder.mMatchName.text = match.name

        // Carga la imagen del perfil del match si la URL no es "default" utilizando Glide
        if (match.profileImageUrl != "default") {
            Glide.with(context).load(match.profileImageUrl).into(holder.mMatchImage)
        }
    }
}