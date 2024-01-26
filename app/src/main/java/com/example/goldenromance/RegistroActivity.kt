package com.example.goldenromance

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.firebase.database.FirebaseDatabase
import com.google.android.gms.tasks.OnCompleteListener
import java.util.HashMap

class RegistroActivity : AppCompatActivity() {

    private lateinit var mRegistro: Button
    private lateinit var spinner: ProgressBar
    private lateinit var mEmail: EditText
    private lateinit var mPassword: EditText
    private lateinit var mName: EditText
    private lateinit var checkBox: CheckBox

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseAuthStateListener: FirebaseAuth.AuthStateListener
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        spinner = findViewById(R.id.pBar)
        spinner.visibility = View.GONE
        val existing = findViewById<TextView>(R.id.existing)
        mAuth = FirebaseAuth.getInstance()
        firebaseAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            spinner.visibility = View.VISIBLE
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null && user.isEmailVerified) {
                val i = Intent(this@RegistroActivity, MainActivity::class.java)
                startActivity(i)
                finish()
                spinner.visibility = View.GONE
                return@AuthStateListener
            }
            spinner.visibility = View.GONE
        }
        existing.setOnClickListener {
            val i = Intent(this@RegistroActivity, MainActivity::class.java)
            startActivity(i)
            finish()
            return@setOnClickListener
        }
        mRegistro = findViewById(R.id.registro)
        mEmail = findViewById(R.id.email)
        mPassword = findViewById(R.id.contrasenya)
        mName = findViewById(R.id.nombre)
        checkBox = findViewById(R.id.checkbox1)
        val textView = findViewById<TextView>(R.id.TextView2)

        checkBox.text = ""
        textView.text = Html.fromHtml("He leído y acepto " + "<a href = 'https://goldenromanceprivacidad.blogspot.com/2024/01/politica-de-privacidad.html'> los Términos y Condiciones</a>")
        textView.isClickable = true
        textView.movementMethod = LinkMovementMethod.getInstance()

        mRegistro.setOnClickListener {
            spinner.visibility = View.VISIBLE

            val email = mEmail.text.toString()
            val password = mPassword.text.toString()
            val name = mName.text.toString()

            val tnc = checkBox.isChecked

            if (checkInputs(email, password, name, tnc)) {
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this@RegistroActivity, OnCompleteListener<AuthResult> { task ->
                        if (!task.isSuccessful) {
                            Toast.makeText(
                                this@RegistroActivity,
                                task.exception?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            mAuth.currentUser?.sendEmailVerification()
                                ?.addOnCompleteListener(OnCompleteListener<Void> { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            this@RegistroActivity,
                                            "Registro hecho con éxito. Por favor, verifica su email para la verificación.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val userId = mAuth.currentUser?.uid
                                        val currentUserDb =
                                            FirebaseDatabase.getInstance().reference
                                                .child("Users").child(userId ?: "")

                                        val userInfo = HashMap<String, Any>()
                                        userInfo["name"] = name
                                        userInfo["profileImageUrl"] = "default"
                                        currentUserDb.updateChildren(userInfo)

                                        mEmail.setText("")
                                        mName.setText("")
                                        mPassword.setText("")

                                        val i = Intent(this@RegistroActivity, LoginYRegistro::class.java)
                                        startActivity(i)
                                        finish()
                                        return@OnCompleteListener
                                    } else {
                                        Toast.makeText(
                                            this@RegistroActivity,
                                            task.exception?.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                        }
                    })
            }
            spinner.visibility = View.GONE
        }
    }

    private fun checkInputs(email: String, username: String, password: String, tnc: Boolean): Boolean {
        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Todos los campos deben ser rellenados", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!email.matches(emailPattern.toRegex())) {
            Toast.makeText(
                this,
                "Email incorrecto, introduce un correo válido y dale a confirmar",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (!tnc) {
            Toast.makeText(this, "Por favor, acepto los Términos y Condiciones", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        return true
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
        val i = Intent(this@RegistroActivity, LoginYRegistro::class.java)
        startActivity(i)
        finish()
    }
}
