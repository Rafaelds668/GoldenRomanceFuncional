package com.example.goldenromance

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lorentzos.flingswipe.SwipeFlingAdapterView


class MainActivity : AppCompatActivity(){
    private lateinit var cardsData: Array<cards>
    private lateinit var cardsArrayAdapter: cardsArrayAdapter<cards>
    private var i: Int = 0

    private lateinit var mAuth: FirebaseAuth
    private lateinit var currentUId: String
    private lateinit var usersDb: DatabaseReference

    private lateinit var listView: ListView
    private lateinit var rowItems: MutableList<cards>
    private lateinit var al : ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        usersDb = FirebaseDatabase.getInstance().reference.child("Users")

        mAuth = FirebaseAuth.getInstance()
        currentUId = mAuth.currentUser?.uid ?: ""

        checkUserSex()

        rowItems = ArrayList()



        cardsArrayAdapter = cardsArrayAdapter(this, R.layout.item, rowItems)

        val flingContainer: SwipeFlingAdapterView = findViewById(R.id.frame)

        flingContainer.adapter = cardsArrayAdapter
        flingContainer.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
            override fun removeFirstObjectInAdapter() {
                Log.d("LIST", "removed object!")
                (rowItems as ArrayList<cards>).removeAt(0)
                cardsArrayAdapter.notifyDataSetChanged()

            }

            override fun onLeftCardExit(dataObject: Any?) {
                val obj = dataObject as cards
                val userId = obj.userId
                usersDb.child(userId!!).child("connections").child("nope").child(currentUId).setValue(true)
                Toast.makeText(this@MainActivity, "DISLIKE", Toast.LENGTH_SHORT).show()

                // Mostrar un banner cuando no hay tarjetas disponibles para mostrar
                val tv = findViewById<TextView>(R.id.noCardsBanner)
                if (rowItems.isEmpty()) {
                    tv.visibility = View.VISIBLE
                } else {
                    tv.visibility = View.INVISIBLE
                }

            }

            override fun onRightCardExit(dataObject: Any?) {
                val obj = dataObject as cards
                val userId = obj.userId
                usersDb.child(userId!!).child("connections").child("yeps").child(currentUId).setValue(true)
                isConnectionMatch(userId)
                Toast.makeText(this@MainActivity, "LIKE", Toast.LENGTH_SHORT).show()

                // Mostrar un banner cuando no hay tarjetas disponibles para mostrar
                val tv = findViewById<TextView>(R.id.noCardsBanner)
                if (rowItems.isEmpty()) {
                    tv.visibility = View.VISIBLE
                } else {
                    tv.visibility = View.INVISIBLE
                }
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

        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(object : SwipeFlingAdapterView.OnItemClickListener {
            override fun onItemClicked(itemPosition: Int, dataObject: Any) {
                Toast.makeText(this@MainActivity, "Item Clicked", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun isConnectionMatch(userId: String) {
        val currentUserConnectionsDb = usersDb.child(currentUId).child("connections").child("yeps").child(userId)
        currentUserConnectionsDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(this@MainActivity, "MATCH", Toast.LENGTH_LONG).show()

                    val key = FirebaseDatabase.getInstance().getReference().child("Chat").push().key

                    usersDb.child(dataSnapshot.key!!).child("connections").child("matches").child(currentUId).child("ChatId").setValue(key)
                    usersDb.child(currentUId).child("connections").child("matches").child(dataSnapshot.key!!).child("ChatId").setValue(key)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private var userSex: String? = null
    private var oppositeUserSex: String? = null

    fun checkUserSex() {
        val user = FirebaseAuth.getInstance().currentUser
        val userDb = usersDb.child(user!!.uid)
        userDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val sex = dataSnapshot.child("sex").getValue(String::class.java)
                    if (sex != null) {
                        userSex = sex
                        when (userSex) {
                            "Male" -> oppositeUserSex = "Female"
                            "Female" -> oppositeUserSex = "Male"
                        }
                        getOppositeSexUsers()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun getOppositeSexUsers() {
        usersDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                if (dataSnapshot.child("sex").getValue(String::class.java) != null) {
                    if (dataSnapshot.exists() &&
                        !dataSnapshot.child("connections").child("nope").hasChild(currentUId) &&
                        !dataSnapshot.child("connections").child("yeps").hasChild(currentUId) &&
                        dataSnapshot.child("sex").getValue(String::class.java) == oppositeUserSex
                    ) {
                        var profileImageUrl = "default"
                        if (dataSnapshot.child("profileImageUrl").getValue(String::class.java) != "default") {
                            profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String::class.java)!!
                        }
                        val item = cards(dataSnapshot.key!!, dataSnapshot.child("name").getValue(String::class.java)!!, profileImageUrl)
                        rowItems.add(item)
                        cardsArrayAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun logoutUser(view: View) {
        mAuth.signOut()
        val intent = Intent(this, LoginYRegistro::class.java)
        startActivity(intent)
        finish()
    }

    fun goToSettings(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    fun goToMatches(view: View) {
        val intent = Intent(this, MatchesActivity::class.java)
        startActivity(intent)
    }

}