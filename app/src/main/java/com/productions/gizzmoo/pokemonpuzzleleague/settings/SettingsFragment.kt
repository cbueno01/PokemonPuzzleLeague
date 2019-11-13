package com.productions.gizzmoo.pokemonpuzzleleague.settings

import android.os.Bundle
import android.preference.PreferenceFragment
import com.productions.gizzmoo.pokemonpuzzleleague.R

class SettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onPause() {
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(findPreference("pref_pokemon_key") as PokemonPreference)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(findPreference("pref_pokemon_key") as PokemonPreference)
    }
}