package com.example.goldenromance.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.goldenromance.MainActivity
import com.example.goldenromance.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    // Declaración de variables para los elementos de la interfaz de usuario
    private lateinit var mLogin: Button
    private lateinit var mEmail: EditText
    private lateinit var mPassword: EditText

    // Declaración de una instancia de FirebaseAuth
    private lateinit var mAuth: FirebaseAuth
    // Declaración de un listener de estado de autenticación de FirebaseAuth
    private lateinit var firebaseAuthStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Establece el diseño de la actividad desde el archivo XML activity_login
        setContentView(R.layout.activity_login)

        // Inicialización de FirebaseAuth
        mAuth = FirebaseAuth.getInstance()

        // Creación del listener de estado de autenticación de FirebaseAuth
        firebaseAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                // Si el usuario está autenticado, se inicia la actividad MainActivity y se finaliza LoginActivity
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
                return@AuthStateListener
            }
        }

        // Inicialización de los elementos de la interfaz de usuario
        mLogin = findViewById(R.id.login)
        mEmail = findViewById(R.id.email)
        mPassword = findViewById(R.id.password)

        // Configuración del listener de clics para el botón de inicio de sesión
        mLogin.setOnClickListener {
            val email = mEmail.text.toString()
            val password = mPassword.text.toString()
            // Intento de inicio de sesión con Firebase Authentication
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (!task.isSuccessful) {
                        // Si el inicio de sesión no tiene éxito, se muestra un mensaje de error
                        Toast.makeText(this@LoginActivity, "Error en el correo", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onStart() {
        super.onStart()
        // Se agrega el listener de estado de autenticación al iniciar la actividad
        mAuth.addAuthStateListener(firebaseAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        // Se remueve el listener de estado de autenticación al detener la actividad
        mAuth.removeAuthStateListener(firebaseAuthStateListener)
    }
}