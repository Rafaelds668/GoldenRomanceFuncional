package com.example.goldenromance.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.goldenromance.MainActivity
import com.example.goldenromance.R
import com.example.goldenromance.auth.LoginActivity
import com.example.goldenromance.auth.LoginYRegistro
import com.google.firebase.auth.FirebaseAuth


class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val user = FirebaseAuth.getInstance().currentUser

        // Crea un nuevo Handler para manejar la ejecución del código después de un retardo
        Handler().postDelayed({
            // Crea un Intent para redirigir a la actividad MainActivity si el usuario está autenticado,
            // de lo contrario, redirige a la actividad LoginYRegistro
            val intent = if (user != null) {
                Intent(this@SplashScreenActivity, MainActivity::class.java)
            } else {
                Intent(this@SplashScreenActivity, LoginYRegistro::class.java)
            }
            // Inicia la actividad correspondiente y finaliza la actividad actual después de 1000 milisegundos (1 segundo)
            startActivity(intent)
            finish()
        }, 1000)


    }
}