package com.example.goldenromance


import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar  // Asegúrate de importar la clase correcta aquí
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.IOException

class SettingsActivity : AppCompatActivity() {
    private lateinit var mNameField: EditText
    private lateinit var mPhoneField: EditText
    private lateinit var mProfileImage: ImageView
    private lateinit var mBack: ImageButton
    private lateinit var mConfirm: Button
    private lateinit var mBudget: EditText
    private lateinit var need: Spinner
    private lateinit var give: Spinner
    private lateinit var spinner: ProgressBar
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUserDatabase: DatabaseReference

    private var userId: String = ""
    private var name: String = ""
    private var phone: String = ""
    private var userSex: String = ""
    private var userBudget: String = ""
    private var userNeed: String = ""
    private var userGive: String = ""
    private var needIndex: Int = 0
    private var giveIndex: Int = 0
    private var resultUri: Uri? = null

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        spinner = findViewById(R.id.pBar)
        spinner.visibility = View.GONE

        mNameField = findViewById(R.id.name)
        mPhoneField = findViewById(R.id.phone)
        mProfileImage = findViewById(R.id.profileImage)
        mBack = findViewById(R.id.settingsBack)
        mConfirm = findViewById(R.id.confirm_button)
        mBudget = findViewById(R.id.budget_settings)
        need = findViewById(R.id.spinner_need_settings)
        give = findViewById(R.id.spinner_give_settings)

        mAuth = FirebaseAuth.getInstance()
        userId = mAuth.currentUser?.uid ?: ""
        if (userId.isEmpty()) {
            finish()
        }

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId)

        val adapterGive: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this,
            R.array.services,
            android.R.layout.simple_spinner_item
        )
        adapterGive.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        give.adapter = adapterGive

        getUserInfo()

        mProfileImage.setOnClickListener {
            if (!checkPermission()) {
                Toast.makeText(this@SettingsActivity, "Por favor, permita el acceso para continuar", Toast.LENGTH_LONG).show()
                requestPermissions()
            } else {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 1)
            }
        }

        mConfirm.setOnClickListener {
            val intent = Intent(this@SettingsActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            return@setOnClickListener
        }

        mBack.setOnClickListener {
            spinner.visibility = View.VISIBLE
            val intent = Intent(this@SettingsActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            return@setOnClickListener
        }

        val toolbar: Toolbar = findViewById(R.id.settings_toolbartag)
        setSupportActionBar(toolbar)

        getUserInfo()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)
        return true
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE), 100)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                //Cambio
                startActivityForResult(intent, 1)
            } else {
                Toast.makeText(this, "Por favor permita el acceso para continuar", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Contactanos
        when (item.itemId) {
            R.id.ContactUs -> {
                AlertDialog.Builder(this@SettingsActivity)
                    .setTitle("Contacanos")
                    .setMessage("Contactanos: goldenRomance@gmail.com")
                    .setNegativeButton("Demiss", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            }
            //Logout
            R.id.logout -> {
                spinner.visibility = View.VISIBLE
                mAuth.signOut()
                Toast.makeText(this, "Log out", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@SettingsActivity, LoginYRegistro::class.java)
                startActivity(intent)
                finish()
                spinner.visibility = View.GONE
            }
            //borrar cuenta
            R.id.deleteAccount -> {
                AlertDialog.Builder(this@SettingsActivity)
                    .setTitle("¿Estas Seguro?")
                    .setMessage("Borrando su cuenta completamente del sistema")
                    .setPositiveButton("Borrar", DialogInterface.OnClickListener { dialog, which ->
                        mAuth.currentUser?.delete()?.addOnCompleteListener { task ->
                            spinner.visibility = View.VISIBLE
                            if (task.isSuccessful) {
                                deleteUserAccount(userId)
                                Toast.makeText(this@SettingsActivity, "Cuenta Borrada con exito", Toast.LENGTH_SHORT)
                                val intent = Intent(this@SettingsActivity, LoginYRegistro::class.java)
                                startActivity(intent)
                                finish()
                                spinner.visibility = View.GONE
                                return@addOnCompleteListener
                            } else {
                                Toast.makeText(this@SettingsActivity, task.exception?.message, Toast.LENGTH_SHORT)
                                mAuth.signOut()
                                val intent = Intent(this@SettingsActivity, LoginYRegistro::class.java)
                                startActivity(intent)
                                finish()
                                spinner.visibility = View.VISIBLE
                                return@addOnCompleteListener
                            }
                        }
                    })
                    .setNegativeButton("Dismiss", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteMatch(matchId: String, chatId: String) {
        val machId_in_UserId_dbReference =
            FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userId).child("connections").child("matches").child(matchId)
        val matchIdId_in_matchId_dbReference =
            FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userId).child("connections").child("matches").child(userId)
        val yeps_in_matchId_dbReference =
            FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userId).child("connections").child("yeps").child(userId)
        val yeps_in_UserId_dbReference =
            FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userId).child("connections").child("yeps").child(matchId)

        val matchId_chat_dbReference = FirebaseDatabase.getInstance().getReference().child("Chat").child(chatId)

        matchId_chat_dbReference.removeValue()
        machId_in_UserId_dbReference.removeValue()
        matchIdId_in_matchId_dbReference.removeValue()
        yeps_in_matchId_dbReference.removeValue()
        yeps_in_UserId_dbReference.removeValue()
    }

    private fun deleteUserAccount(userId: String) {
        val curreser_ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userId)
        val curruser_matches_ref = FirebaseDatabase.getInstance().getReference().child("Users")
            .child(userId).child("connections").child("matches")

        curruser_matches_ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (match in snapshot.children) {
                        deleteMatch(match.key ?: "", match.child("ChatId").value.toString())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        curruser_matches_ref.removeValue()
        curreser_ref.removeValue()
    }

    private fun getUserInfo() {
        mUserDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                    val map = dataSnapshot.value as Map<*, *>
                    if (map["nombre"] != null) {
                        name = map["nombre"].toString()
                        mNameField.setText(name)
                    }
                    if (map["phone"] != null) {
                        phone = map["phone"].toString()
                        mPhoneField.setText(phone)
                    }
                    if (map["sex"] != null) {
                        userSex = map["sex"].toString()
                    }
                    if (map["budget"] != null) {
                        userBudget = map["budget"].toString()
                    } else
                        userBudget = "0"
                    if (map["give"] != null) {
                        userGive = map["give"].toString()
                    } else
                        userGive = ""
                    if (map["need"] != null) {
                        userNeed = map["need"].toString()
                    } else
                        userNeed = ""

                    val services = resources.getStringArray(R.array.services)
                    needIndex  = 0
                    giveIndex = 0
                    for (i in services.indices) {
                        if (userNeed == services[i])
                            needIndex = 1
                        if (userGive == services[i])
                            giveIndex = 1
                    }

                    need.setSelection(needIndex)
                    give.setSelection(giveIndex)
                    mBudget.setText(userBudget)

                    Glide.clear(mProfileImage)
                    if(map["profileImageUrl"] != null){
                        val profileImageUrl = map["profileImageUrl"].toString()
                        when(profileImageUrl){
                            "default" -> Glide.with(application).load(R.drawable.profile).into(mProfileImage)
                            else -> Glide.with(application).load(profileImageUrl).into(mProfileImage)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun saveUserInformation() {
        name = mNameField.text.toString()
        phone = mPhoneField.text.toString()
        userBudget = mBudget.text.toString()
        userGive = give.selectedItem.toString()
        userNeed = need.selectedItem.toString()

        val userInfo: MutableMap<String, Any> = HashMap()
        userInfo["nombre"] = name
        userInfo["phone"] = phone
        userInfo["need"] = userNeed
        userInfo["give"] = userGive
        userInfo["budget"] = userBudget
        mUserDatabase.updateChildren(userInfo)
        if (resultUri != null) {
            val filepath: StorageReference =
                FirebaseStorage.getInstance().getReference().child("profileImages").child(userId)
            var bitmap: Bitmap? = null

            try {
                bitmap =
                    MediaStore.Images.Media.getBitmap(application.contentResolver, resultUri)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val baos = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 20, baos)
            val data = baos.toByteArray()
            val uploadTask = filepath.putBytes(data)
            uploadTask.addOnFailureListener {
                finish()
                uploadTask.addOnSuccessListener { taskSnapshot ->
                    val uri = taskSnapshot.storage.downloadUrl
                    while (!uri.isComplete);
                    val downloadUri = uri.result
                    val userInfo: MutableMap<String, Any> = HashMap()
                    userInfo["profileImageUrl"] = downloadUri.toString()
                    mUserDatabase.updateChildren(userInfo)

                    finish()
                    return@addOnSuccessListener
                }
            }
        } else {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            resultUri = imageUri
            mProfileImage.setImageURI(resultUri)
        }
    }
}
