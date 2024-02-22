package com.example.goldenromance.BTN

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.goldenromance.MainActivity
import com.example.goldenromance.R

class BtnDislikeActivity : AppCompatActivity() {

    // Declaración de variables
    private val TAG = "BtnDisLikeActivity"
    private val ACTIVITY_NUM = 1
    private lateinit var mContext: Context
    private lateinit var dislike: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Establece el diseño de la actividad desde el archivo XML activity_btn_dislike
        setContentView(R.layout.activity_btn_dislike)

        // Inicialización del contexto
        mContext = this
        // Inicialización del ImageView para el botón de "dislike"
        dislike = findViewById(R.id.dislike)

        // Obtiene la URL del perfil del intento
        val intent = intent
        val profileUrl = intent.getStringExtra("url")

        // Carga la imagen del perfil utilizando Glide, si la URL es "default", carga una imagen predeterminada
        when (profileUrl) {
            "default" -> Glide.with(mContext).load(R.drawable.profile).into(dislike)
            else -> Glide.with(mContext).load(profileUrl).into(dislike)
        }

        // Crea un hilo para dormir la actividad durante 1 segundo y luego iniciar la actividad principal
        Thread {
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            val mainIntent = Intent(this@BtnDislikeActivity, MainActivity::class.java)
            startActivity(mainIntent)
        }.start()
    }
}