package com.productions.gizzmoo.pokemonpuzzleleague.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Created by Chrystian on 4/7/2018.
 */

public class SettingsActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
        SharedPreferences settings = getApplicationContext().getSharedPreferences("PreferencesName", Context.MODE_PRIVATE);
        settings.edit().clear().apply();
    }
}
