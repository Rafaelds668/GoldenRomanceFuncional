package com.example.goldenromance.auth

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.goldenromance.MainActivity
import com.example.goldenromance.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.firebase.database.FirebaseDatabase
import com.google.android.gms.tasks.OnCompleteListener
import java.util.HashMap

class RegistroActivity : AppCompatActivity() {

    private lateinit var mRegister: Button
    private lateinit var mEmail: EditText
    private lateinit var mPassword: EditText
    private lateinit var mName: EditText
    private lateinit var mRadioGroup: RadioGroup

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseAuthStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        mAuth = FirebaseAuth.getInstance()

        firebaseAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val intent = Intent(this@RegistroActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
                return@AuthStateListener
            }
        }

        mRegister = findViewById(R.id.registro)
        mEmail = findViewById(R.id.email)
        mPassword = findViewById(R.id.contrasenya)
        mName = findViewById(R.id.name)
        mRadioGroup = findViewById(R.id.radioGroup)

        mRegister.setOnClickListener {
            val selectId = mRadioGroup.checkedRadioButtonId
            val radioButton = findViewById<RadioButton>(selectId)

            if (radioButton.text == null) {
                return@setOnClickListener
            }

            val email = mEmail.text.toString()
            val password = mPassword.text.toString()
            val name = mName.text.toString()
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this@RegistroActivity) { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(this@RegistroActivity, "sign up error", Toast.LENGTH_SHORT).show()
                    } else {
                        val userId = mAuth.currentUser?.uid
                        val currentUserDb = FirebaseDatabase.getInstance().reference.child("Users").child(userId!!)
                        val userInfo: MutableMap<String, Any> = HashMap()
                        userInfo["name"] = name
                        userInfo["sex"] = radioButton.text.toString()
                        userInfo["profileImageUrl"] = "default"
                        currentUserDb.updateChildren(userInfo)
                    }
                }
        }
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(firebaseAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        mAuth.removeAuthStateListener(firebaseAuthStateListener)
    }

}