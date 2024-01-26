package com.example.goldenromance.Matches;

import com.example.goldenromance.UserObject.UserObject;

import java.util.ArrayList;

public class MatchObject {
    private String userId, name, profileImageUrl, need, give, budget, lastMessage, lastTimeStamp, lastSeen, childId;
    private ArrayList<UserObject> userObjectArrayList = new ArrayList<>();

    public MatchObject(String userId, String name, String profileImageUrl, String need, String give, String budget, String lastMessage, String lastTimeStamp, String lastSeen, String childId) {
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.need = need;
        this.give = give;
        this.budget = budget;
        this.lastMessage = lastMessage;
        this.lastTimeStamp = lastTimeStamp;
        this.lastSeen = lastSeen;
        this.childId = childId;
    }

    public ArrayList<UserObject> getUserObjectArrayList(){
        return userObjectArrayList;
    }

    public void addUserToArrayList(UserObject mUser){

    }
}
