package com.example.goldenromance

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.goldenromance.databinding.ActivitySettingsBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.HashMap

class SettingsActivity : AppCompatActivity() {

    // Declaración de variables miembro
    private lateinit var mNameField: EditText
    private lateinit var mPhoneField: EditText
    private lateinit var mBack: Button
    private lateinit var mConfirm: Button
    private lateinit var mProfileImage: ImageView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUserDatabase: DatabaseReference
    private lateinit var userId: String
    private var name: String = ""
    private var phone: String = ""
    private var profileImageUrl: String = ""
    private var userSex: String = ""
    private var resultUri: Uri? = null

    // Método onCreate que se llama cuando se crea la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Inicialización de vistas y Firebase
        mNameField = findViewById(R.id.name)
        mPhoneField = findViewById(R.id.phone)
        mProfileImage = findViewById(R.id.profileImage)
        mBack = findViewById(R.id.back)
        mConfirm = findViewById(R.id.confirm)
        mAuth = FirebaseAuth.getInstance()
        userId = mAuth.currentUser!!.uid
        mUserDatabase = FirebaseDatabase.getInstance().reference.child("Users").child(userId)

        // Obtener información del usuario
        getUserInfo()

        // Configurar clic en la imagen de perfil para seleccionar una nueva imagen
        mProfileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        // Guardar la información del usuario cuando se hace clic en el botón de confirmar
        mConfirm.setOnClickListener {
            saveUserInformation()
        }

        // Volver a la actividad anterior cuando se hace clic en el botón de retroceso
        mBack.setOnClickListener {
            finish()
        }
    }

    // Método para obtener información del usuario desde la base de datos
    private fun getUserInfo() {
        mUserDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                    val map: Map<String, Any> = dataSnapshot.value as Map<String, Any>
                    name = map["name"].toString()
                    mNameField.setText(name)
                    phone = map["phone"].toString()
                    mPhoneField.setText(phone)
                    userSex = map["sex"].toString()
                    if (map["profileImageUrl"] != null) {
                        profileImageUrl = map["profileImageUrl"].toString()
                        when (profileImageUrl) {
                            "default" -> Glide.with(application).load(R.mipmap.ic_launcher).into(mProfileImage)
                            else -> Glide.with(application).load(profileImageUrl).into(mProfileImage)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    // Método para guardar la información del usuario en la base de datos
    private fun saveUserInformation() {
        // Obtener nombre y teléfono del usuario
        name = mNameField.text.toString()
        val phoneInput = mPhoneField.text.toString()

        //Verificar si el numero de telefono tiene nueve digitos
        if(phoneInput.length != 9){
            //Mostrar el mensaje de error
            mPhoneField.error = "El número de telefono debe ser de 9 digitos"
            return
        }

        //Si el numero de telefono tiene 9 digitos, continuar con la actualizacion de la información del usuario
        phone = phoneInput

        // Actualizar la información del usuario en la base de datos
        val userInfo: MutableMap<String, Any> = HashMap()
        userInfo["name"] = name
        userInfo["phone"] = phone
        mUserDatabase.updateChildren(userInfo)

        // Subir la imagen de perfil si se ha seleccionado una nueva
        if (resultUri != null) {
            val filepath = FirebaseStorage.getInstance().reference.child("profileImages").child(userId)
            var bitmap: Bitmap? = null
            try {
                bitmap = MediaStore.Images.Media.getBitmap(application.contentResolver, resultUri)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val baos = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 20, baos)
            val data = baos.toByteArray()
            val uploadTask = filepath.putBytes(data)
            uploadTask.addOnFailureListener { finish() }
            uploadTask.addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    val userInfo: MutableMap<String, Any> = HashMap()
                    userInfo["profileImageUrl"] = downloadUrl
                    mUserDatabase.updateChildren(userInfo)
                    finish()
                }
            }
        } else {
            finish()
        }
    }

    // Método llamado cuando se completa una actividad iniciada para obtener una imagen de perfil
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            resultUri = imageUri
            mProfileImage.setImageURI(resultUri)
        }
    }
}
