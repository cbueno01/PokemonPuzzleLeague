package com.productions.gizzmoo.pokemonpuzzleleague.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.productions.gizzmoo.pokemonpuzzleleague.R;

/**
 * Created by Chrystian on 4/9/2018.
 */

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener((PokemonPreference) findPreference("pref_pokemon_key"));
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener((PokemonPreference) findPreference("pref_pokemon_key"));
    }
}
