package com.productions.gizzmoo.pokemonpuzzleleague.settings

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.preference.Preference
import androidx.preference.PreferenceDialogFragmentCompat
import com.productions.gizzmoo.pokemonpuzzleleague.R
import com.productions.gizzmoo.pokemonpuzzleleague.settings.NumberPreference.Companion.DEFAULT_VALUE

class NumberDialog : PreferenceDialogFragmentCompat() {
    private var pickerValue = DEFAULT_VALUE

    override fun onCreateDialogView(context: Context): View {
        context.theme?.applyStyle(R.style.SettingsDialogFragment, true)

        val preference = preference as NumberPreference
        pickerValue = preference.value

        return NumberPicker(context).apply {
            wrapSelectorWheel = false
            maxValue = 50
            minValue = 1
            value = pickerValue
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setOnValueChangedListener { _, _, newVal ->  pickerValue = newVal}
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val preference = preference as NumberPreference
            preference.persistNumber(pickerValue)
        }
    }

    companion object {
        fun newInstance(preference: Preference) : NumberDialog {
            val fragment = NumberDialog()
            val bundle = Bundle(1)
            bundle.putString(ARG_KEY, preference.key)
            fragment.arguments = bundle
            return fragment
        }
    }
}