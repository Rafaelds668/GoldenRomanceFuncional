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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        mNameField = findViewById(R.id.name)
        mPhoneField = findViewById(R.id.phone)
        mProfileImage = findViewById(R.id.profileImage)
        mBack = findViewById(R.id.back)
        mConfirm = findViewById(R.id.confirm)

        mAuth = FirebaseAuth.getInstance()
        userId = mAuth.currentUser!!.uid

        mUserDatabase = FirebaseDatabase.getInstance().reference.child("Users").child(userId)

        getUserInfo()

        mProfileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        mConfirm.setOnClickListener {
            saveUserInformation()
        }

        mBack.setOnClickListener {
            finish()
        }
    }

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

    private fun saveUserInformation() {
        name = mNameField.text.toString()
        phone = mPhoneField.text.toString()

        val userInfo: MutableMap<String, Any> = HashMap()
        userInfo["name"] = name
        userInfo["phone"] = phone
        mUserDatabase.updateChildren(userInfo)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            resultUri = imageUri
            mProfileImage.setImageURI(resultUri)
        }
    }
}
