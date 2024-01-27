package com.example.goldenromance.Matches

import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goldenromance.MainActivity
import com.example.goldenromance.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MatchesActivity : AppCompatActivity() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMatchesAdapter: RecyclerView.Adapter<*>
    private lateinit var mMatchesLayoutManager: RecyclerView.LayoutManager
    private lateinit var mBack: ImageButton
    private lateinit var current: DatabaseReference
    private lateinit var lsiten: ValueEventListener
    private val mList: HashMap<String, Int> = HashMap()
    private lateinit var currentUserId: String
    private lateinit var mLastTimeStamp: String
    private lateinit var mLastMessage: String
    private lateinit var lastSend: String
    private lateinit var mCurrUserIdInsideMatchConnections: DatabaseReference
    private lateinit var mCheckLastSeen: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matches)

        mBack = findViewById(R.id.matchesBack)
        currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

        mRecyclerView = findViewById(R.id.recyclerView)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.setHasFixedSize(true)
        mMatchesLayoutManager = LinearLayoutManager(this)
        mRecyclerView.layoutManager = mMatchesLayoutManager
        mMatchesAdapter = MatchesAdapter(getDataSetMatches(), this)
        mRecyclerView.adapter = mMatchesAdapter

        mBack.setOnClickListener {
            val intent = Intent(this@MatchesActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        getUserMatchId()
        mLastMessage = ""
        mLastTimeStamp = ""
        lastSend = ""
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    private fun getLastMessageInfo(userDb: DatabaseReference) {
        mCurrUserIdInsideMatchConnections = userDb.child("connections").child("matches").child(currentUserId)

        mCurrUserIdInsideMatchConnections.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("lastMessage").value != null &&
                        dataSnapshot.child("lastTimeStamp").value != null &&
                        dataSnapshot.child("lastSend").value != null
                    ) {
                        mLastMessage = dataSnapshot.child("lastMessage").value.toString()
                        mLastTimeStamp = dataSnapshot.child("lastTimeStamp").value.toString()
                        lastSend = dataSnapshot.child("lastSend").value.toString()
                    } else {
                        mLastMessage = "Empezar a chatear"
                        mLastTimeStamp = " "
                        lastSend = "true"
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled if needed
            }
        })
    }

    private fun getUserMatchId() {
        val sortedMatchesByLastTimeStamp = FirebaseDatabase.getInstance().getReference()
            .child("Users").child(currentUserId).child("connections").child("matches")
            .orderByChild("LastTimeStamp")

        sortedMatchesByLastTimeStamp.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (match in dataSnapshot.children) {
                        fetchMatchInformation(match.key!!, match.child("ChatId").getValue(String::class.java)!!)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled if needed
            }
        })
    }

    private fun fetchMatchInformation(key: String, chatId: String) {
        val userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(key)
        getLastMessageInfo(userDb)

        userDb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val userId = dataSnapshot.key
                    var name = ""
                    var profileImageUrl = ""
                    var need = ""
                    var give = ""
                    var budget = ""
                    var lastMessage = ""
                    var lastTimeStamp = ""

                    if (dataSnapshot.child("name").value != null) {
                        name = dataSnapshot.child("name").value.toString()
                    }

                    if (dataSnapshot.child("profileImageUrl").value != null) {
                        profileImageUrl = dataSnapshot.child("profileImageUrl").value.toString()
                    }

                    if (dataSnapshot.child("need").value != null) {
                        need = dataSnapshot.child("need").value.toString()
                    }

                    if (dataSnapshot.child("give").value != null) {
                        give = dataSnapshot.child("give").value.toString()
                    }
                    if (dataSnapshot.child("budget").value != null) {
                        budget = dataSnapshot.child("budget").value.toString()
                    }

                    val milliSec = mLastTimeStamp
                    var now: Long
                    try {
                        now = milliSec.toLong()
                        lastTimeStamp = convertMilliToRelative(now)
                        val arrOfStr = lastTimeStamp.split(",")
                        mLastTimeStamp = arrOfStr[0]
                    } catch (e: Exception) {}

                    val obj = MatchesObject(
                        userId,
                        name,
                        profileImageUrl,
                        need,
                        give,
                        budget,
                        mLastMessage,
                        mLastTimeStamp,
                        chatId,
                        lastMessage
                    )
                    if (mList.containsKey(chatId)) {
                        val key = mList[chatId]!!
                        resultsMatches[resultsMatches.size - key] = obj
                    } else {
                        resultsMatches.add(0, obj)
                        mList[chatId] = resultsMatches.size
                    }
                    mMatchesAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled if needed
            }
        })
    }

    private fun convertMilliToRelative(now: Long): String {
        val time = DateUtils.getRelativeDateTimeString(
            this,
            now,
            DateUtils.SECOND_IN_MILLIS,
            DateUtils.WEEK_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_ALL
        ).toString()
        return time
    }

    private val resultsMatches: ArrayList<MatchesObject> = ArrayList()

    private fun getDataSetMatches(): List<MatchesObject> {
        return resultsMatches
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


}