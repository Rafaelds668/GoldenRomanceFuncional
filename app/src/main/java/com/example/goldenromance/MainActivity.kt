package com.example.goldenromance

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.goldenromance.implementaciones.BottonNavigationViewEx.BottomNavigationViewEx
import com.example.goldenromance.implementaciones.showCaseView.ShowCaseView
import com.example.goldenromance.implementaciones.showCaseView.position.ShowCasePosition
import com.example.goldenromance.implementaciones.showCaseView.position.ViewPosition
import com.example.goldenromance.implementaciones.showCaseView.radius.Radius
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private var firstStart: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       setContentView(R.layout.activity_main)

        setupToNavigationView()

    }

    private fun setupToNavigationView() {
        val tvEx: BottomNavigationView = findViewById(R.id.topNavigationViewBar)
        TopNavigationViewHelper.setupTopNavigationView(tvEx)
        TopNavigationViewHelper.enableNavigation(this@MainActivity, tvEx)
        val menu: Menu = tvEx.menu
        val menuItem: MenuItem = menu.getItem(1)
        menuItem.isChecked = true

        val profileView: View = findViewById(R.id.ic_profile)
        val matchesView: View = findViewById(R.id.ic_matched)

        if (firstStart) {
            showToolTipProfile(ViewPosition(profileView))
        }

        val newPref: SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = newPref.edit()
        editor.putBoolean("firstStart", false)
        editor.apply()
    }

    private fun showToolTipProfile(position: ShowCasePosition) {
        ShowCaseView.Builder(this@MainActivity)
            .withTypedPosition(position)
            .withTypedRadius(Radius(186F))
            .withContent("La primera vez sube tu foto de perfil y haz clic en confirmar. De lo contrario, tu aplicación no funciona bien.")
            .build()
            .show(this@MainActivity)
    }

}