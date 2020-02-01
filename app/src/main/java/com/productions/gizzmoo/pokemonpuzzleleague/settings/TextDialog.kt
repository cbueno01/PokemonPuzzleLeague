package com.productions.gizzmoo.pokemonpuzzleleague.settings

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceDialogFragmentCompat
import com.productions.gizzmoo.pokemonpuzzleleague.R
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.puzzleacademygame.FileManager

class TextDialog : PreferenceDialogFragmentCompat() {
    override fun onCreateDialogView(context: Context): View {
        context.theme?.applyStyle(R.style.SettingsDialogFragment, true)

        return TextView(context).apply {
            text = "Are you sure you want to reset your data?"
            textAlignment = TEXT_ALIGNMENT_CENTER
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val jsonReaderWriter = FileManager.getJSONReaderWriter(context!!, 1)
            jsonReaderWriter.deleteJSONFile(context!!, 1)
        }
    }

    companion object {
        fun newInstance(preference: Preference) : TextDialog {
            val fragment =  TextDialog()
            val bundle = Bundle(1)
            bundle.putString(ARG_KEY, preference.key)
            fragment.arguments = bundle
            return fragment
        }
    }
}