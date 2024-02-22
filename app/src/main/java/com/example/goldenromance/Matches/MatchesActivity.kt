package com.example.goldenromance.Matches

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.goldenromance.R
import java.util.ArrayList

class MatchesActivity : AppCompatActivity() {

    // Declaración de variables
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMatchesAdapter: RecyclerView.Adapter<*>
    private lateinit var mMatchesLayoutManager: RecyclerView.LayoutManager

    private lateinit var currentUserId: String

    // Método onCreate, se ejecuta cuando se crea la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marches) // Establece el diseño de la actividad desde el archivo XML activity_marches

        // Obtiene el ID del usuario actual
        currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

        // Inicializa el RecyclerView y sus componentes
        mRecyclerView = findViewById(R.id.recyclerView)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.setHasFixedSize(true)
        mMatchesLayoutManager = LinearLayoutManager(this)
        mRecyclerView.layoutManager = mMatchesLayoutManager
        mMatchesAdapter = MatchesAdapter(getDataSetMatches(), this)
        mRecyclerView.adapter = mMatchesAdapter

        // Obtiene los IDs de los matches del usuario actual
        getUserMatchId()
    }

    // Método para obtener los IDs de los matches del usuario actual desde la base de datos
    private fun getUserMatchId() {
        val matchDb = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId).child("connections").child("matches")
        matchDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (match in dataSnapshot.children) {
                        fetchMatchInformation(match.key) // Obtiene la información de cada match
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    // Método para obtener información de cada match desde la base de datos
    private fun fetchMatchInformation(key: String?) {
        val userDb = FirebaseDatabase.getInstance().reference.child("Users").child(key!!)
        userDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val userId = dataSnapshot.key
                    var name = ""
                    var profileImageUrl = ""
                    if (dataSnapshot.child("name").value != null) {
                        name = dataSnapshot.child("name").value.toString()
                    }
                    if (dataSnapshot.child("profileImageUrl").value != null) {
                        profileImageUrl = dataSnapshot.child("profileImageUrl").value.toString()
                    }
                    // Crea un objeto MatchesObject con la información obtenida y lo añade a la lista de resultados
                    val obj = MatchesObject(userId.toString(), name, profileImageUrl)
                    resultsMatches.add(obj)
                    mMatchesAdapter.notifyDataSetChanged() // Notifica al adaptador de cambios en los datos
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    // Lista de matches obtenidos
    private val resultsMatches: ArrayList<MatchesObject> = ArrayList()
    private fun getDataSetMatches(): List<MatchesObject> {
        return resultsMatches
    }
}