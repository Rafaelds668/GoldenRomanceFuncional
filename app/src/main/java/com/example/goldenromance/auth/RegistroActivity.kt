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
    // Declaración de variables para los elementos de la interfaz de usuario
    private lateinit var mRegister: Button
    private lateinit var mEmail: EditText
    private lateinit var mPassword: EditText
    private lateinit var mName: EditText
    private lateinit var mRadioGroup: RadioGroup

    // Declaración de una instancia de FirebaseAuth
    private lateinit var mAuth: FirebaseAuth
    // Declaración de un listener de estado de autenticación de FirebaseAuth
    private lateinit var firebaseAuthStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Establece el diseño de la actividad desde el archivo XML activity_registro
        setContentView(R.layout.activity_registro)

        // Inicialización de FirebaseAuth
        mAuth = FirebaseAuth.getInstance()

        // Creación del listener de estado de autenticación de FirebaseAuth
        firebaseAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                // Si el usuario está autenticado, se inicia la actividad MainActivity y se finaliza RegistroActivity
                val intent = Intent(this@RegistroActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
                return@AuthStateListener
            }
        }

        // Inicialización de los elementos de la interfaz de usuario
        mRegister = findViewById(R.id.registro)
        mEmail = findViewById(R.id.email)
        mPassword = findViewById(R.id.contrasenya)
        mName = findViewById(R.id.name)
        mRadioGroup = findViewById(R.id.radioGroup)

        // Configuración del listener de clics para el botón de registro
        mRegister.setOnClickListener {
            // Obtención del ID del RadioButton seleccionado en el RadioGroup
            val selectId = mRadioGroup.checkedRadioButtonId
            val radioButton = findViewById<RadioButton>(selectId)

            // Verificación de que se haya seleccionado un RadioButton
            if (radioButton.text == null) {
                return@setOnClickListener
            }

            // Obtención de los valores de correo electrónico, contraseña y nombre
            val email = mEmail.text.toString().trim()
            val password = mPassword.text.toString().trim()
            val name = mName.text.toString()

            //Validar la contraseña para que no se contenga espacios ni caracteres especiales
            val passwordPattern = "[a-zA-Z0-9]+"
            if (!password.matches(passwordPattern.toRegex())) {
                // Si la contraseña no coincide con el patrón, mostrar un mensaje de error
                Toast.makeText(this@RegistroActivity, "La contraseña no puede contener espacios ni caracteres especiales", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            // Creación de un nuevo usuario con Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this@RegistroActivity) { task ->
                    if (!task.isSuccessful) {
                        // Si la creación de usuario no tiene éxito, se muestra un mensaje de error
                        Toast.makeText(this@RegistroActivity, "sign up error", Toast.LENGTH_SHORT).show()
                    } else {
                        // Si la creación de usuario tiene éxito, se guarda la información adicional del usuario en la base de datos
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
        // Se agrega el listener de estado de autenticación al iniciar la actividad
        mAuth.addAuthStateListener(firebaseAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        // Se remueve el listener de estado de autenticación al detener la actividad
        mAuth.removeAuthStateListener(firebaseAuthStateListener)
    }
}