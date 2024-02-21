package com.example.goldenromance

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.IOException

class SettingsActivity : AppCompatActivity() {

    private lateinit var mNameField: EditText
    private lateinit var mPhoneField: EditText
    private lateinit var mProfileImage: ImageView
    private lateinit var mBack: Button
    private lateinit var mConfirm: Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUserDatabase: DatabaseReference

    private lateinit var userId: String
    private lateinit var name: String
    private lateinit var phone: String
    private lateinit var profileImageUrl: String
    private lateinit var userSex: String
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
            return@setOnClickListener
        }
    }

    private fun getUserInfo() {
        mUserDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                    val map = dataSnapshot.value as Map<*, *>
                    name = map["name"].toString()
                    mNameField.setText(name)
                    phone = map["phone"].toString()
                    mPhoneField.setText(phone)
                    userSex = map["sex"].toString()
                    if (map["profileImageUrl"] != null) {
                        profileImageUrl = map["profileImageUrl"].toString()
                        when (profileImageUrl) {
                            "default" -> Glide.with(applicationContext).load(R.mipmap.ic_launcher).into(mProfileImage)
                            else -> Glide.with(applicationContext).load(profileImageUrl).into(mProfileImage)
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

        val userInfo = HashMap<String, Any>()
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
            uploadTask.addOnSuccessListener(OnSuccessListener { taskSnapshot ->
                val downloadUrl = taskSnapshot.storage.downloadUrl

                val userInfo = HashMap<String, Any>()
                userInfo["profileImageUrl"] = downloadUrl.toString()
                mUserDatabase.updateChildren(userInfo)

                finish()
                return@OnSuccessListener
            })
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
