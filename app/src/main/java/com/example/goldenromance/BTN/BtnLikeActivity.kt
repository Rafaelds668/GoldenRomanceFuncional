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

    private val TAG = "BtnLikeActivity"
    private val ACTIVITY_NUM = 1
    private lateinit var mContext: Context
    private lateinit var like: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_btn_like)

        mContext = this
        like = findViewById(R.id.like)

        val intent = intent
        val profileUrl = intent.getStringExtra("url")

        when (profileUrl) {
            "default" -> Glide.with(mContext).load(R.drawable.profile).into(like)
            else -> Glide.with(mContext).load(profileUrl).into(like)
        }

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