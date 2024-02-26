package com.example.goldenromance.Chat


import com.example.goldenromance.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mChatAdapter: RecyclerView.Adapter<*>
    private lateinit var mChatLayoutManager: RecyclerView.LayoutManager
    private lateinit var mSendEditText: EditText
    private lateinit var mSendButton: Button
    private lateinit var currentUserID: String
    private lateinit var matchId: String
    private lateinit var chatId: String
    private lateinit var mDatabaseUser: DatabaseReference
    private lateinit var mDatabaseChat: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        matchId = intent.extras!!.getString("matchId")?: ""

        currentUserID = FirebaseAuth.getInstance().currentUser!!.uid

        mDatabaseUser = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(currentUserID)
            .child("connections")
            .child("matches")
            .child(matchId)
            .child("ChatId")

        mDatabaseChat = FirebaseDatabase.getInstance().reference.child("Chat")

        getChatId()

        mRecyclerView = findViewById(R.id.recyclerView)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.setHasFixedSize(false)
        mChatLayoutManager = LinearLayoutManager(this)
        mRecyclerView.layoutManager = mChatLayoutManager
        mChatAdapter = ChatAdapter(getDataSetChat(), this)
        mRecyclerView.adapter = mChatAdapter

        mSendEditText = findViewById(R.id.message)
        mSendButton = findViewById(R.id.send)

        mSendButton.setOnClickListener {
            sendMessage()
        }
    }

    private fun sendMessage() {
        val sendMessageText = mSendEditText.text.toString()

        if (sendMessageText.isNotEmpty()) {
            val newMessageDb = mDatabaseChat.push()

            val newMessage: MutableMap<String, String> = HashMap()
            newMessage["createdByUser"] = currentUserID
            newMessage["text"] = sendMessageText

            newMessageDb.setValue(newMessage)
        }
        mSendEditText.setText(null)
    }

    private fun getChatId() {
        mDatabaseUser.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    chatId = dataSnapshot.value.toString()
                    mDatabaseChat = mDatabaseChat.child(chatId)
                    getChatMessages()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun getChatMessages() {
        mDatabaseChat.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                if (dataSnapshot.exists()) {
                    var message: String? = null
                    var createdByUser: String? = null

                    message = dataSnapshot.child("text").value as? String
                    createdByUser = dataSnapshot.child("createdByUser").value as? String

                    if (message != null && createdByUser != null) {
                        var currentUserBoolean = false
                        if (createdByUser == currentUserID) {
                            currentUserBoolean = true
                        }
                        val newMessage = ChatObject(message, currentUserBoolean)
                        resultsChat.add(newMessage)
                        mChatAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private val resultsChat: ArrayList<ChatObject> = ArrayList()
    private fun getDataSetChat(): List<ChatObject> {
        return resultsChat
    }
}