package com.example.goldenromance.BTN

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.goldenromance.MainActivity
import com.example.goldenromance.R

class BtnLikeActivity : AppCompatActivity() {

    // Declaración de variables
    private val TAG = "BtnLikeActivity"
    private val ACTIVITY_NUM = 1
    private lateinit var mContext: Context
    private lateinit var like: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Establece el diseño de la actividad desde el archivo XML activity_btn_like
        setContentView(R.layout.activity_btn_like)

        // Inicialización del contexto
        mContext = this
        // Inicialización del ImageView para el botón de "like"
        like = findViewById(R.id.like)

        // Obtiene la URL del perfil del intento
        val intent = intent
        val profileUrl = intent.getStringExtra("url")

        // Carga la imagen del perfil utilizando Glide, si la URL es "default", carga una imagen predeterminada
        when (profileUrl) {
            "default" -> Glide.with(mContext).load(R.drawable.profile).into(like)
            else -> Glide.with(mContext).load(profileUrl).into(like)
        }

        // Crea un hilo para dormir la actividad durante 1 segundo y luego iniciar la actividad principal
        Thread {
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            val mainIntent = Intent(this@BtnLikeActivity, MainActivity::class.java)
            startActivity(mainIntent)
        }.start()
    }
}