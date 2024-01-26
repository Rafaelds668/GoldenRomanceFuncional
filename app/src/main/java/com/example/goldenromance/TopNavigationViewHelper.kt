package com.example.goldenromance

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.MenuItem
import com.example.goldenromance.implementaciones.BottonNavigationViewEx.BottomNavigationViewEx
import com.google.android.material.bottomnavigation.BottomNavigationView

class TopNavigationViewHelper {

    companion object {
        private  val TAG = "TopNavigationViewHelper"
        fun setupTopNavigationView(tvEx: BottomNavigationView) {
            Log.d(TAG, "setupTopNavigationView: setting up navigationview")
        }
        fun enableNavigation(context: Context, view: BottomNavigationView) {
            view.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.ic_profile -> {
                        val i = Intent(context, SettingsActivity::class.java)
                        context.startActivity(i)
                    }
                    R.id.ic_matched -> {
                        val i1 = Intent(context, MatchesActivity::class.java)
                        context.startActivity(i1)
                    }
                }
                false
            }
        }
    }
}

