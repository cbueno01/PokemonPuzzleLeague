package com.productions.gizzmoo.pokemonpuzzleleague.settings

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.productions.gizzmoo.pokemonpuzzleleague.R

class SettingsActivity : Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Display the fragment as the main content.
        fragmentManager.beginTransaction().replace(android.R.id.content, SettingsFragment()).commit()
        val settings = applicationContext.getSharedPreferences("PreferencesName", Context.MODE_PRIVATE)
        settings.edit().clear().apply()
    }
}