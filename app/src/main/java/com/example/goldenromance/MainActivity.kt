package com.example.goldenromance

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.goldenromance.Cards.cardsArrayAdapter
import com.example.goldenromance.Cards.cards
import com.example.goldenromance.Matches.MatchesActivity
import com.example.goldenromance.auth.LoginYRegistro
import com.example.goldenromance.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lorentzos.flingswipe.SwipeFlingAdapterView


class MainActivity : AppCompatActivity(){
    // Declaración de variables miembro
    private lateinit var mAuth: FirebaseAuth
    private lateinit var currentUId: String
    private lateinit var usersDb: DatabaseReference
    private lateinit var rowItems: MutableList<cards>
    private lateinit var cardsArrayAdapter: cardsArrayAdapter<cards>
    private lateinit var binding: ActivityMainBinding

    // Método onCreate que se llama cuando se crea la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        binding.navBotton.setOnItemSelectedListener {item->
//
//            when (item.itemId) {
//                R.id.ic_logout -> {
//                    //Redirigir la actividad de inciio de sesion
//                    val intent = Intent(this, LoginYRegistro::class.java)
//                    startActivity(intent)
//                    true
//                }
//
//                R.id.ic_matches -> {
//                    val intent = Intent(this, MatchesActivity::class.java)
//                    startActivity(intent)
//                    true
//                }
//
//                R.id.ic_perfil -> {
//                    val intent = Intent(this, SettingsActivity::class.java)
//                    intent.putExtra("Profile_user_id", FirebaseAuth.getInstance().currentUser?.uid)
//                    startActivity(intent)
//                    true
//                }
//
//                else -> false
//            }
//        }

        // Inicialización de Firebase Auth y DatabaseReference
        mAuth = FirebaseAuth.getInstance()
        currentUId = mAuth.currentUser?.uid ?: ""
        usersDb = FirebaseDatabase.getInstance().reference.child("Users")

        // Verificar el género del usuario actual y obtener usuarios del sexo opuesto
        checkUserSex()

        // Inicialización de la lista de tarjetas y su adaptador
        rowItems = ArrayList()
        cardsArrayAdapter = cardsArrayAdapter(this, R.layout.item, rowItems)

        // Configuración del SwipeFlingAdapterView
        val flingContainer: SwipeFlingAdapterView = findViewById(R.id.frame)
        flingContainer.adapter = cardsArrayAdapter
        flingContainer.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
            // Método llamado cuando se elimina el primer objeto en el adaptador
            override fun removeFirstObjectInAdapter() {
                (rowItems as ArrayList<cards>).removeAt(0)
                cardsArrayAdapter.notifyDataSetChanged()
            }

            // Métodos llamados cuando una tarjeta es arrastrada hacia la izquierda o hacia la derecha
            override fun onLeftCardExit(dataObject: Any?) {
                handleLeftSwipe(dataObject as cards)
            }

            override fun onRightCardExit(dataObject: Any?) {
                handleRightSwipe(dataObject as cards)
            }

            override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {}
            override fun onScroll(scrollProgressPercent: Float) {
                val view = flingContainer?.selectedView
                val rightIndicator = view?.findViewById<View>(R.id.item_swipe_right_indicator)
                val leftIndicator = view?.findViewById<View>(R.id.item_swipe_left_indicator)

                rightIndicator?.alpha = if (scrollProgressPercent != 0f && scrollProgressPercent < 0) -scrollProgressPercent else 0f
                leftIndicator?.alpha = if (scrollProgressPercent != 0f && scrollProgressPercent > 0) scrollProgressPercent else 0f
            }
        })

        // Configuración del OnItemClickListener para el SwipeFlingAdapterView
        flingContainer.setOnItemClickListener { _, _, _, _ ->
            Toast.makeText(this@MainActivity, "Item Clicked", Toast.LENGTH_SHORT).show()
        }


    }

    // Método para manejar el deslizamiento hacia la izquierda (dislike) de una tarjeta
    private fun handleLeftSwipe(card: cards) {
        usersDb.child(card.userId!!).child("connections").child("nope").child(currentUId).setValue(true)
        Toast.makeText(this@MainActivity, "DISLIKE", Toast.LENGTH_SHORT).show()
        checkNoCards()
    }

    // Método para manejar el deslizamiento hacia la derecha (like) de una tarjeta
    private fun handleRightSwipe(card: cards) {
        usersDb.child(card.userId!!).child("connections").child("yeps").child(currentUId).setValue(true)
        isConnectionMatch(card.userId)
        Toast.makeText(this@MainActivity, "LIKE", Toast.LENGTH_SHORT).show()
        checkNoCards()
    }

    // Método para verificar si hay coincidencias entre usuarios
    private fun isConnectionMatch(userId: String?) {
        // Verificar si el usuario actual y el usuario especificado tienen un "yep" en común
        val currentUserConnectionsDb = usersDb.child(currentUId).child("connections").child("yeps").child(userId!!)
        currentUserConnectionsDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Si existe una conexión, mostrar un mensaje de coincidencia
                    Toast.makeText(this@MainActivity, "MATCH", Toast.LENGTH_LONG).show()
                    // Generar un ChatId único para la nueva conversación
                    val key = FirebaseDatabase.getInstance().reference.child("Chat").push().key
                    // Establecer los ChatId para ambos usuarios en la base de datos
                    usersDb.child(dataSnapshot.key!!).child("connections").child("matches").child(currentUId).child("ChatId").setValue(key)
                    usersDb.child(currentUId).child("connections").child("matches").child(dataSnapshot.key!!).child("ChatId").setValue(key)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    // Método para verificar el sexo del usuario actual
    private var userSex: String? = null
    private var oppositeUserSex: String? = null
    private fun checkUserSex() {
        // Obtener el género del usuario actual desde la base de datos
        val user = FirebaseAuth.getInstance().currentUser
        val userDb = usersDb.child(user!!.uid)
        userDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Si el usuario tiene un género especificado en la base de datos, obtenerlo y definir el sexo opuesto
                    val sex = dataSnapshot.child("sex").getValue(String::class.java)
                    if (sex != null) {
                        userSex = sex
                        oppositeUserSex = if (userSex == "Male") "Female" else "Male"
                        // Obtener usuarios del sexo opuesto
                        getOppositeSexUsers()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    // Método para obtener usuarios del sexo opuesto
    private fun getOppositeSexUsers() {
        // Escuchar eventos de agregación de usuarios en la base de datos
        usersDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                // Verificar si el usuario tiene un sexo especificado y es del sexo opuesto al usuario actual
                if (dataSnapshot.child("sex").getValue(String::class.java) != null &&
                    dataSnapshot.exists() &&
                    !dataSnapshot.child("connections").child("nope").hasChild(currentUId) &&
                    !dataSnapshot.child("connections").child("yeps").hasChild(currentUId) &&
                    dataSnapshot.child("sex").getValue(String::class.java) == oppositeUserSex
                ) {
                    // Obtener la URL de la imagen de perfil del usuario
                    var profileImageUrl = "default"
                    if (dataSnapshot.child("profileImageUrl").getValue(String::class.java) != "default") {
                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String::class.java)!!
                    }
                    // Crear una tarjeta con la información del usuario y agregarla a la lista de tarjetas
                    val item = cards(dataSnapshot.key!!, dataSnapshot.child("name").getValue(String::class.java)!!, profileImageUrl)
                    rowItems.add(item)
                    cardsArrayAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    // Método para cerrar sesión del usuario actual
    fun logoutUser(view: View) {
        mAuth.signOut()
        val intent = Intent(this, LoginYRegistro::class.java)
        startActivity(intent)
        finish()
    }

    // Método para ir a la pantalla de ajustes
    fun goToSettings(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    // Método para ir a la pantalla de matches
    fun goToMatches(view: View) {
        val intent = Intent(this, MatchesActivity::class.java)
        startActivity(intent)
    }

    // Método para verificar si no hay más tarjetas disponibles para mostrar
    private fun checkNoCards() {
        val tv = findViewById<TextView>(R.id.noCardsBanner)
        tv.visibility = if (rowItems.isEmpty()) View.VISIBLE else View.INVISIBLE
    }
}