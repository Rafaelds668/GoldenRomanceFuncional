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

        Handler().postDelayed({
            val intent = if (user != null) {
                Intent(this@SplashScreenActivity, MainActivity::class.java)
            } else {
                Intent(this@SplashScreenActivity, LoginYRegistro::class.java)
            }
            startActivity(intent)
            finish()
        }, 1000)


    }
}