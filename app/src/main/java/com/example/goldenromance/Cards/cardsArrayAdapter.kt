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
    // Override del método getView para personalizar la vista de cada elemento en el ListView
    override fun getView(position: Int, convertView: View?, parent: ViewGroup) : View{
        var convertViewVar = convertView
        val cardItem = getItem(position)

        // Si la vista convertida es nula, inflarla desde el archivo de diseño "item"
        if (convertViewVar == null){
            convertViewVar = LayoutInflater.from(context).inflate(R.layout.item, parent, false)
        }

        // Obtener referencias a los elementos de la vista
        val name = convertViewVar!!.findViewById<TextView>(R.id.name)
        val image = convertViewVar.findViewById<ImageView>(R.id.image)

        // Establecer el nombre y la imagen del cardItem en los elementos correspondientes
        name.text = cardItem!!.name
        when(cardItem!!.profileImageUrl){
            // Si la URL de la imagen es "default", cargar la imagen predeterminada
            "default" -> Glide.with(convertViewVar.context).load(R.mipmap.ic_launcher).into(image)
            // De lo contrario, cargar la imagen desde la URL proporcionada
            else ->{
                // Limpiar la imagen actual
                Glide.with(convertViewVar.context).clear(image)
                // Cargar la imagen desde la URL
                Glide.with(convertViewVar.context).load(cardItem!!.profileImageUrl).into(image)
            }
        }

        return convertViewVar
    }
}
