package com.productions.gizzmoo.pokemonpuzzleleague.settings

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.productions.gizzmoo.pokemonpuzzleleague.R
import android.graphics.drawable.ColorDrawable

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setDivider(ColorDrawable(resources.getColor(R.color.primary)))
        setDividerHeight(getDpiForDivider())
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        when (preference) {
            is TrainerPreference -> showFragmentDialog(TrainerDialog.newInstance(preference))
            is PokemonPreference -> showFragmentDialog(PokemonDialog.newInstance(preference))
            is NumberPreference -> showFragmentDialog(NumberDialog.newInstance(preference))
            else -> super.onDisplayPreferenceDialog(preference)
        }
    }

    override fun onPause() {
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(findPreference("pref_pokemon_key") as PokemonPreference)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(findPreference("pref_pokemon_key") as PokemonPreference)
    }

    private fun showFragmentDialog(fragment: DialogFragment) {
        fragment.setTargetFragment(this, 0)
        fragment.show(fragmentManager, "android.support.v7.preference.PreferenceFragment.DIALOG")
    }

    private fun getDpiForDivider(): Int =
            (DIVIDER_SIZE * resources.displayMetrics.density + 0.5f).toInt()

    companion object {
        const val DIVIDER_SIZE = 1
    }
}