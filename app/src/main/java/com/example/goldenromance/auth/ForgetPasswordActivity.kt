package com.example.goldenromance.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.goldenromance.R
import com.google.firebase.auth.FirebaseAuth

class ForgetPasswordActivity : AppCompatActivity() {
    private lateinit var mForgotPasswordButton: Button
    private lateinit var mEmail: EditText
    private lateinit var mAuth: FirebaseAuth
    private var flag = 0
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        mAuth = FirebaseAuth.getInstance()
        flag = 0
        mForgotPasswordButton = findViewById(R.id.resetPasswordButton)
        mEmail = findViewById(R.id.resetPasswordEmail)

        mForgotPasswordButton.setOnClickListener {
            email = mEmail.text.toString()
            if (email.isEmpty()) {
                Toast.makeText(this@ForgetPasswordActivity, "Correo está vacío.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Verifica si el correo electrónico es válido
            if (!email.matches(emailPattern.toRegex())) {
                Toast.makeText(this@ForgetPasswordActivity, "Correo inválido. Introduzca un email correcto.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
                flag = 1
                mAuth.sendPasswordResetEmail(mEmail.text.toString()).addOnCompleteListener { resetTask ->
                    if (resetTask.isSuccessful) {
                        Toast.makeText(this@ForgetPasswordActivity, "Instrucciones para restablecer la contraseña enviadas al email", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@ForgetPasswordActivity, resetTask.exception?.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
            if (flag == 0) {
                Toast.makeText(this@ForgetPasswordActivity, "Email no encontrado", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onBackPressed() {
        val btnClick = Intent(this@ForgetPasswordActivity, LoginActivity::class.java)
        startActivity(btnClick)
        super.onBackPressed()
        finish()
        return
    }

}