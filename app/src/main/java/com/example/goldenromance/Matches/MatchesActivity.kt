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

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMatchesAdapter: RecyclerView.Adapter<*>
    private lateinit var mMatchesLayoutManager: RecyclerView.LayoutManager
    private lateinit var cusrrentUserID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marches)

        cusrrentUserID = FirebaseAuth.getInstance().currentUser!!.uid

        mRecyclerView = findViewById(R.id.recyclerView)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.setHasFixedSize(true)
        mMatchesLayoutManager = LinearLayoutManager(this@MatchesActivity)
        mRecyclerView.layoutManager = mMatchesLayoutManager
        mMatchesAdapter = MatchesAdapter(getDataSetMatches(), this@MatchesActivity)
        mRecyclerView.adapter = mMatchesAdapter

        getUserMatchId()
    }
    private fun getUserMatchId() {
        val matchDb = FirebaseDatabase.getInstance().reference.child("Users").child(cusrrentUserID).child("connections").child("matches")
        matchDb.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
               if (dataSnapshot.exists()){
                   for (match in dataSnapshot.children){
                       fetchMatchInformation(match.key!!)
                   }
               }
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun fetchMatchInformation(key: String) {
        val userDb = FirebaseDatabase.getInstance().reference.child("Users").child(key)
        userDb.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    val userId = dataSnapshot.key
                    var name = ""
                    var profileImageUrl = ""
                    if(dataSnapshot.child("name").value != null){
                        name = dataSnapshot.child("name").value.toString()
                    }
                    if (dataSnapshot.child("profileImageUrl").value != null){
                        profileImageUrl = dataSnapshot.child("profileImageUrl").value.toString()
                    }

                    val obj = MatchesObject(userId.toString(), name, profileImageUrl)
                    resultMatches.add(obj)
                    mMatchesAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private val resultMatches = ArrayList<MatchesObject>()
    private fun getDataSetMatches(): List<MatchesObject> {
        return resultMatches
    }
}