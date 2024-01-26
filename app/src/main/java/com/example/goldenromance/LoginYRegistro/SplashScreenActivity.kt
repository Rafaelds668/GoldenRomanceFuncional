package com.example.goldenromance.LoginYRegistro

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.goldenromance.R

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        Handler().postDelayed({
            val intent = Intent(this@SplashScreenActivity, LoginYRegistro::class.java)
            startActivity(intent)
            finish()
        }, 1000)

    }
}
