package com.example.goldenromance.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.goldenromance.R
import com.example.goldenromance.auth.LoginActivity

class LoginYRegistro : AppCompatActivity() {

    // Declaración de variables para los botones de inicio de sesión y registro
    private lateinit var mLogin: Button
    private lateinit var mRegistro: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Establece el diseño de la actividad desde el archivo XML activity_login_yregistro
        setContentView(R.layout.activity_login_yregistro)

        // Inicialización de los botones de inicio de sesión y registro
        mLogin = findViewById(R.id.login)
        mRegistro = findViewById(R.id.registro)

        // Configuración del listener de clics para el botón de inicio de sesión
        mLogin.setOnClickListener {
            // Cuando se hace clic en el botón de inicio de sesión, se crea un intento para iniciar la actividad LoginActivity
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
        }

        // Configuración del listener de clics para el botón de registro
        mRegistro.setOnClickListener {
            // Cuando se hace clic en el botón de registro, se crea un intento para iniciar la actividad RegistroActivity (no proporcionado en el código)
            val i = Intent(this, RegistroActivity::class.java)
            startActivity(i)
        }
    }
}