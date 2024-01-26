package com.example.goldenromance

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class LoginYRegistro : AppCompatActivity() {

    private lateinit var mLogin: Button
    private lateinit var mRegistro: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_yregistro)

        mLogin = findViewById(R.id.login)
        mRegistro = findViewById(R.id.registro)

        mLogin.setOnClickListener {
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
        }

        mRegistro.setOnClickListener {
            val i = Intent(this, RegistroActivity::class.java)
            startActivity(i)
        }
    }
}
