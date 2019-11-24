package com.productions.gizzmoo.pokemonpuzzleleague.settings

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import androidx.preference.Preference
import androidx.preference.PreferenceDialogFragmentCompat
import com.productions.gizzmoo.pokemonpuzzleleague.R
import com.productions.gizzmoo.pokemonpuzzleleague.settings.PokemonPreference.Companion.DEFAULT_ID

class PokemonDialog : PreferenceDialogFragmentCompat() {
    private var pokemonID = DEFAULT_ID

    override fun onCreateDialogView(context: Context): View {
        context.theme?.applyStyle(R.style.SettingsDialogFragment, true)

        val preference = preference as PokemonPreference
        pokemonID = preference.pokemonID

        val portraitAdapter = ImagePortraitAdapter(context, preference.bitmaps)
        portraitAdapter.chosenPosition = pokemonID

        val gridView = GridView(context).apply {
            numColumns = 3
            gravity = Gravity.CENTER
            isVerticalScrollBarEnabled = false
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        gridView.adapter = portraitAdapter
        gridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            pokemonID = position
            portraitAdapter.chosenPosition = position
            portraitAdapter.notifyDataSetChanged()
        }

        return gridView
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val preference = preference as PokemonPreference
            preference.persistPokemonID(pokemonID)
        }
    }

    companion object {
        fun newInstance(preference: Preference) : PokemonDialog {
            val fragment = PokemonDialog()
            val bundle = Bundle(1)
            bundle.putString(ARG_KEY, preference.key)
            fragment.arguments = bundle
            return fragment
        }
    }
}