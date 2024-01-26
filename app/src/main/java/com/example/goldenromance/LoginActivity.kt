package com.example.goldenromance

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var spinner: ProgressBar
    private lateinit var mLogin: Button
    private lateinit var mEmail: EditText
    private lateinit var mPassword: EditText
    private lateinit var mForgetPassword: TextView
    private var loginClicked: Boolean = false
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseAuthStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginClicked = false
        spinner = findViewById(R.id.pBar)
        spinner.visibility = View.GONE

        mAuth = FirebaseAuth.getInstance()
        mLogin = findViewById(R.id.login)
        mEmail = findViewById(R.id.email)
        mPassword = findViewById(R.id.password)
        mForgetPassword = findViewById(R.id.recordarContraseñaBoton)

        mLogin.setOnClickListener {
            loginClicked = true
            spinner.visibility = View.VISIBLE
            val email = mEmail.text.toString()
            val password = mPassword.text.toString()

            if (isStringNull(email) || isStringNull(password)) {
                Toast.makeText(
                    this@LoginActivity,
                    "Debes llenar todos los campos",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this@LoginActivity,
                        OnCompleteListener<AuthResult> { task ->
                            if (!task.isSuccessful) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    task.exception?.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                if (mAuth.currentUser?.isEmailVerified == true) {
                                    val i = Intent(
                                        this@LoginActivity,
                                        MainActivity::class.java
                                    )
                                    startActivity(i)
                                    finish()
                                    return@OnCompleteListener
                                } else {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Por favor verifica el correo.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        })
            }
        }

        mForgetPassword.setOnClickListener {

            spinner.visibility = View.VISIBLE
            val i = Intent(this@LoginActivity, ForgetPasswordActivity::class.java)
            startActivity(i)
            finish()
            return@setOnClickListener
        }
        firebaseAuthStateListener = FirebaseAuth.AuthStateListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null && user.isEmailVerified && !loginClicked) {
                spinner.visibility = View.VISIBLE
                val i = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(i)
                finish()
                spinner.visibility = View.GONE
                return@AuthStateListener
            }
        }
    }
    private fun isStringNull(email: String?): Boolean {
        return email.equals("")
    }
    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(firebaseAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        mAuth.removeAuthStateListener(firebaseAuthStateListener)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(this@LoginActivity, LoginYRegistro::class.java)
        startActivity(i)
        finish()
    }
}
