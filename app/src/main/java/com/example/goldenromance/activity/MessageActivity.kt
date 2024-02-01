package com.example.goldenromance.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.goldenromance.databinding.ActivityMessageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.checkerframework.checker.units.qual.Current
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMessageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageView2.setOnClickListener {
            if (binding.yourMessage.text!!.isEmpty()){
                Toast.makeText(this, "Escribe su mensaje", Toast.LENGTH_SHORT).show()
            }else{

            }
        }
    }

    private fun sendMessage(msg: String) {

        val receiverId = intent.getStringExtra("userId")
        val senderId = FirebaseAuth.getInstance().currentUser!!.phoneNumber

        val chatId= senderId+receiverId
        val currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val currentTime: String = SimpleDateFormat("HH:mm:a", Locale.getDefault()).format(Date())


        val map = hashMapOf<String, String>()
        map["message"] = msg
        map["senderId"] = senderId!!
        map["currentTime"] = currentTime
        map["currentDate"] = currentDate


        FirebaseDatabase.getInstance().getReference("chats").child(chatId)
            .setValue(map).addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(this, "Mensaje enviado", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "Algo ha fallado", Toast.LENGTH_SHORT).show()

                }
            }
    }
}