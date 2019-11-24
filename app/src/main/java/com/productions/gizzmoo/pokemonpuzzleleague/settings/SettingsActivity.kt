package com.productions.gizzmoo.pokemonpuzzleleague.settings

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Display the fragment as the main content.
        supportFragmentManager.beginTransaction().replace(android.R.id.content, SettingsFragment()).commit()
        val settings = applicationContext.getSharedPreferences("PreferencesName", Context.MODE_PRIVATE)
        settings.edit().clear().apply()
    }
}