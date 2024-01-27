package com.example.goldenromance.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.goldenromance.Matches.MatchesActivity
import com.example.goldenromance.R
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatActivity : AppCompatActivity() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mChatAdapter: RecyclerView.Adapter<*>
    private lateinit var mChatLayoutManager: RecyclerView.LayoutManager

    private lateinit var mSendEditText: EditText
    private lateinit var mBack: ImageButton

    private lateinit var mSendButton: ImageButton
    private lateinit var notification: String
    private lateinit var currentUserId: String
    private lateinit var matchId: String
    private lateinit var chatId: String
    private lateinit var lastMessage: String
    private lateinit var lastTimeStamp: String
    private lateinit var matchName: String
    private lateinit var matchGive: String
    private lateinit var matchNeed: String
    private lateinit var matchBudget: String
    private lateinit var matchProfile: String
    private var currentUserBoolean: Boolean = false
    private lateinit var message: String
    private lateinit var createdByUser: String
    private lateinit var isSeen: String
    private lateinit var messageId: String
    private lateinit var currentUserName: String
    private lateinit var seenListener: ValueEventListener
    private lateinit var mDatabaseUser: DatabaseReference
    private lateinit var mDatabaseChat: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        matchId = intent.extras?.getString("matchId") ?: ""
        matchName = intent.extras?.getString("matchName") ?: ""
        matchGive = intent.extras?.getString("give") ?: ""
        matchNeed = intent.extras?.getString("need") ?: ""
        matchBudget = intent.extras?.getString("budget") ?: ""
        matchProfile = intent.extras?.getString("profile") ?: ""

        mDatabaseUser = FirebaseDatabase.getInstance().reference.child("Users")
            .child(currentUserId).child("connections").child(matchId).child("ChatId")
        mDatabaseChat = FirebaseDatabase.getInstance().reference.child("Chat")

        getChatId()

        mRecyclerView = findViewById(R.id.recyclerView)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isFocusable = false
        mChatLayoutManager = LinearLayoutManager(this@ChatActivity)
        mRecyclerView.layoutManager = mChatLayoutManager
        mChatAdapter = ChatAdapter(getDataSetChat(), this@ChatActivity)
        mRecyclerView.adapter = mChatAdapter

        mSendEditText = findViewById(R.id.message)
        mBack = findViewById(R.id.chatBack)

        mSendButton = findViewById(R.id.send)

        mSendButton.setOnClickListener {
            sendMessage()
        }

        mRecyclerView.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom) {
                mRecyclerView.postDelayed({
                    mRecyclerView.smoothScrollToPosition(mRecyclerView.adapter!!.itemCount - 1)
                }, 100)
            }
        }

        mBack.setOnClickListener {
            val i = Intent(this@ChatActivity, MatchesActivity::class.java)
            startActivity(i)
            finish()
            return@setOnClickListener
        }

        val toolbar = findViewById<Toolbar>(R.id.chatToolbar)
        setSupportActionBar(toolbar)

        val reference = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId)
        val onChat = HashMap<String, Any>()
        onChat["onChat"] = matchId
        reference.updateChildren(onChat)

        val current = FirebaseDatabase.getInstance().reference.child("Users")
            .child(matchId).child("connections").child("matches").child(currentUserId)
        val lastSeen = HashMap<String, Any>()
        lastSeen["lastSeen"] = false
        current.updateChildren(lastSeen)
    }

    override fun onPause() {
        val reference = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId)
        val onChat = hashMapOf<String, Any>(
            "onChat" to "None"
        )
        reference.updateChildren(onChat)
        super.onPause()
    }

    override fun onStop() {
        val reference = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId)
        val onChat = hashMapOf<String, Any>(
            "onChat" to "None"
        )
        reference.updateChildren(onChat)
        super.onStop()
    }

    private fun seenMessage(text: String) {
        val reference = FirebaseDatabase.getInstance().getReference("Users").child(matchId)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("onChat").exists()) {
                        val notification = dataSnapshot.child("notificationKey").getValue(String::class.java) ?: ""
                        if (dataSnapshot.child("onChat").getValue(String::class.java) != currentUserId) {
                            SendNotification(
                                text,
                                "Nuevo mensaje de: $currentUserName",
                                notification,
                                "activityToBeOpened",
                                "MatchesActivity"
                            )
                        } else {
                            val reference = FirebaseDatabase.getInstance().getReference().child("Users")
                                .child(currentUserId).child("connections").child("matches").child(matchId)
                            val seenInfo = hashMapOf<String, Any>(
                                "lastSend" to false
                            )
                            reference.updateChildren(seenInfo)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el error de cancelación
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.chat_menu, menu)
        val mMatchNameTextView = findViewById<TextView>(R.id.chatToolbar)
        mMatchNameTextView.text = matchName
        return true
    }

    fun showProfile(v: View) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.item_profile, null)

        var name = popupView.findViewById<TextView>(R.id.name)
        val image = popupView.findViewById<ImageView>(R.id.image)
        var budget = popupView.findViewById<TextView>(R.id.budget)
        val mNeedIMage = popupView.findViewById<ImageView>(R.id.needImage)
        val mGiveIMage = popupView.findViewById<ImageView>(R.id.giveImage)

        name = matchName
        budget = matchBudget


        when (matchNeed) {
            "Petanca" -> mNeedIMage.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.petanca))
            "Telenovelas" -> mNeedIMage.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.telenovelas))
            "Leer" -> mNeedIMage.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.leer))
            "Bailar" -> mNeedIMage.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.bailar))
            "Bingo" -> mNeedIMage.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.bingo))
            "Chinchon" -> mNeedIMage.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.chinchon))
            "Cinquillo" -> mNeedIMage.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.cinquillo))
            "Brisca" -> mNeedIMage.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.brisca))
            else -> mNeedIMage.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.none))
        }

        when (matchGive) {
            "Petanca" -> mGiveIMage.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.petanca))
            "Telenovelas" -> mGiveIMage.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.telenovelas))
            "Leer" -> mGiveIMage.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.leer))
            "Bailar" -> mGiveIMage.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.bailar))
            "Bingo" -> mGiveIMage.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.bingo))
            "Chinchon" -> mGiveIMage.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.chinchon))
            "Cinquillo" -> mGiveIMage.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.cinquillo))
            "Brisca" -> mGiveIMage.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.brisca))
            else -> mGiveIMage.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.none))
        }

        when (matchProfile) {
            "default" -> Glide.with(popupView.context).load(R.drawable.profile).into(image)
            else -> {
                Glide.with(popupView.context).clear(image)
                Glide.with(popupView.context).load(matchProfile).into(image)
            }
        }

        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        val focusable = true
        val popupWindow = PopupWindow(popupView, width, height, focusable)

        hideSoftKeyBoard()

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0)
        popupView.setOnToutchListener{v: View, event: MotionEvent ->
            popupWindow.dismiss()
            false
        }

    }

    private fun hideSoftKeyBoard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isAcceptingText) {
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.unmatch -> {
                AlertDialog.Builder(this@ChatActivity)
                    .setTitle("Unmatch")
                    .setMessage("¿Estás seguro de hacer unmatch?")
                    .setPositiveButton("Unmatch") { dialog, _ ->
                        deleteMatch(matchId)
                        val intent = Intent(this@ChatActivity, MatchesActivity::class.java)
                        startActivity(intent)
                        finish()
                        Toast.makeText(this@ChatActivity, "Unmatch Realizado", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Desmiss", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
                true
            }
            R.id.viewProfile -> {
                showProfile(findViewById<View>(androidx.constraintlayout.widget.R.id.wrap_content_constrained))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteMatch(matchId: String) {
        val machId_in_UserId_dbReference =
                FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(currentUserId).child("connections").child("matches").child(matchId)
        val matchIdId_in_matchId_dbReference =
                FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(matchId).child("connections").child("matches").child(currentUserId)
        val yeps_in_matchId_dbReference =
                FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(matchId).child("connections").child("yeps").child(currentUserId)
        val yeps_in_UserId_dbReference =
                FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(currentUserId).child("connections").child("yeps").child(matchId)

        val matchId_chat_dbReference = FirebaseDatabase.getInstance().getReference().child("Chat").child(chatId)

        matchId_chat_dbReference.removeValue()
        machId_in_UserId_dbReference.removeValue()
        matchIdId_in_matchId_dbReference.removeValue()
        yeps_in_matchId_dbReference.removeValue()
        yeps_in_UserId_dbReference.removeValue()

    }

    fun sendMessage() {
        val sendMessageText = mSendEditText.text.toString()
        val now = System.currentTimeMillis()
        val timeStamp = now.toString()

        if (sendMessageText.isNotEmpty()) {
            val newMessageDb = mDatabaseChat.push()

            val newMessage = hashMapOf(
                "createdByUser" to currentUserId,
                "text" to sendMessageText,
                "timeStamp" to timeStamp,
                "ssen" to "false"
            )

            val ref = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.child("name").exists()) {
                        currentUserName = dataSnapshot.child("name").getValue().toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejar el error de cancelación
                }
            })

            lastMessage = sendMessageText
            lastTimeStamp = timeStamp
            updateLastMessage()
            seenMessage(sendMessageText)
            newMessageDb.setValue(newMessage)
        }
        mSendEditText.text = null
    }

    private fun updateLastMessage() {
        val currUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId)
            .child("connections").child("matches").child(matchId)
        val matchDb = FirebaseDatabase.getInstance().getReference().child("Users").child(matchId)
            .child("connections").child("matches").child(currentUserId)

        val lastMessageMap = hashMapOf(
            "lastMessage" to lastMessage
        )
        val lastTimestampMap = hashMapOf(
            "lastTimeStamp" to lastTimeStamp
        )

        val lastSeen = hashMapOf(
            "lastSeen" to "true"
        )
        currUserDb.updateChildren(lastSeen as Map<String, Any>)
        currUserDb.updateChildren(lastMessageMap as Map<String, Any>)
        currUserDb.updateChildren(lastTimestampMap as Map<String, Any>)

        matchDb.updateChildren(lastMessageMap as Map<String, Any>)
        matchDb.updateChildren(lastTimestampMap as Map<String, Any>)

    }

    private fun getChatId() {
        mDatabaseUser.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    chatId = dataSnapshot.getValue(String::class.java) ?: ""
                    mDatabaseChat = mDatabaseChat.child(chatId)
                    getChatMessages()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el error de cancelación
            }
        })
    }

    private fun getChatMessages() {
        mDatabaseChat.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                if (dataSnapshot.exists()) {
                    var messageId: String? = null
                    var message: String? = null
                    var createdByUser: String? = null
                    var isSeen: String? = null

                    dataSnapshot.child("text").getValue(String::class.java)?.let {
                        message = it
                    }
                    dataSnapshot.child("createdByUser").getValue(String::class.java)?.let {
                        createdByUser = it
                    }
                    dataSnapshot.child("seen").getValue(String::class.java)?.let {
                        isSeen = it
                    } ?: run { isSeen = "true" }

                    messageId = dataSnapshot.key

                    val newMessage: ChatObject?
                    if (message != null && createdByUser != null) {
                        var currentUserBoolean = false
                        if (createdByUser == currentUserId) {
                            currentUserBoolean = true
                        }
                        newMessage = if (isSeen == "false") {
                            if (!currentUserBoolean) {
                                isSeen = "true"

                                val reference = FirebaseDatabase.getInstance().getReference().child("Chat")
                                    .child(chatId).child(messageId!!)
                                val seenInfo = hashMapOf<String, Any>(
                                    "seen" to "true"
                                )
                                reference.updateChildren(seenInfo)
                                null
                            } else {
                                ChatObject(message!!, currentUserBoolean, true)
                            }
                        } else {
                            ChatObject(message!!, currentUserBoolean, true)
                        }
                    } else {
                        newMessage = ChatObject(message ?: "", false, true)

                        val userInChat = FirebaseDatabase.getInstance().getReference().child("Chat").child(matchId)

                        resultChat.add(newMessage)
                        mChatAdapter.notifyDataSetChanged()
                        if (mRecyclerView.adapter != null && resultChat.size > 0) {
                            mRecyclerView.smoothScrollToPosition(resultChat.size - 1)
                        } else {
                            Toast.makeText(this@ChatActivity, "Chat Vacio", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private val resultChat = ArrayList<ChatObject>()

    private fun getDataSetChat(): List<ChatObject> {
        return resultChat
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}