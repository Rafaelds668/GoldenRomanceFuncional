package com.example.goldenromance.chat;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goldenromance.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActivityChat extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;

    private EditText mSendEditText;
    private ImageButton mBack;

    private ImageButton mSendButton;
    private String notification;
    private String currentUserId, matchId, chatId;
    private String lastMessage, lastTimeStamp;
    private String matchName, matchGive, matchNeed, matchBudget, matchProfile;
    private Boolean currentUserBoolean;
    private String message, createdByUser, isSeen, messageId, currentUserName;
    ValueEventListener seenListener;
    DatabaseReference mDatabaseUser, mDatabaseChat;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        matchId = getIntent().getExtras().getString("matchId");
        matchName = getIntent().getExtras().getString("matchName");
        matchGive = getIntent().getExtras().getString("give");
        matchNeed = getIntent().getExtras().getString("need");
        matchBudget = getIntent().getExtras().getString("budget");
        matchProfile = getIntent().getExtras().getString("profile");

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(currentUserId).child("connections").child(matchId).child("ChatId");
    }
}
