package com.productions.gizzmoo.pokemonpuzzleleague;

import android.os.Bundle;
import android.preference.PreferenceFragment;

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
}
