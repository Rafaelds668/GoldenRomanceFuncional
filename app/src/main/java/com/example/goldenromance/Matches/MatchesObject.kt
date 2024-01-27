package com.example.goldenromance.Matches

import com.example.goldenromance.UserObject.UserObject

class MatchesObject(
    var userId: String?,
    var name: String,
    var profileImageUrl: String,
    var need: String,
    var give: String,
    var budget: String,
    var lastMessage: String,
    var lastTimeStamp: String,
    var lastSeen: String,
    var childId: String
){

    var userObjectArrayList: ArrayList<UserObject> = ArrayList()

    fun addUserToArrayList(mUser: UserObject) {
        userObjectArrayList.add(mUser)
    }
}