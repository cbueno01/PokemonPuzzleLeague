package com.productions.gizzmoo.pokemonpuzzleleague.settings

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import androidx.preference.Preference
import androidx.preference.PreferenceDialogFragmentCompat
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import com.productions.gizzmoo.pokemonpuzzleleague.R
import com.productions.gizzmoo.pokemonpuzzleleague.settings.TrainerPreference.Companion.DEFAULT_ID

class TrainerDialog : PreferenceDialogFragmentCompat() {
    private var trainerID = DEFAULT_ID

    override fun onCreateDialogView(context: Context): View {
        context.theme?.applyStyle(R.style.SettingsDialogFragment, true)

        val preference = preference as TrainerPreference
        trainerID = preference.trainerID

        val portraitAdapter = ImagePortraitAdapter(context, preference.bitmaps)
        portraitAdapter.chosenPosition = trainerID

        val gridView = GridView(context).apply {
            numColumns = 2
            gravity = Gravity.CENTER
            isVerticalScrollBarEnabled = false
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        gridView.adapter = portraitAdapter
        gridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            trainerID = position
            portraitAdapter.chosenPosition = trainerID
            portraitAdapter.notifyDataSetChanged()
        }

        return gridView
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val preference = preference as TrainerPreference
            preference.persistTrainerID(trainerID)
        }
    }

    companion object {
        fun newInstance(preference: Preference) : TrainerDialog {
            val fragment = TrainerDialog()
            val bundle = Bundle(1)
            bundle.putString(ARG_KEY, preference.key)
            fragment.arguments = bundle
            return fragment
        }
    }
}