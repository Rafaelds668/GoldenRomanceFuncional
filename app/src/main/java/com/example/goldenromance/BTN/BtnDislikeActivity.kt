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

    private val TAG = "BtnDisLikeActivity"
    private val ACTIVITY_NUM = 1
    private lateinit var mContext: Context
    private lateinit var dislike: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_btn_dislike)


        mContext = this
        dislike = findViewById(R.id.dislike)
        val intent = intent
        val profileUrl = intent.getStringExtra("url")

        when (profileUrl) {
            "default" -> Glide.with(mContext).load(R.drawable.profile).into(dislike)
            else -> Glide.with(mContext).load(profileUrl).into(dislike)
        }

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