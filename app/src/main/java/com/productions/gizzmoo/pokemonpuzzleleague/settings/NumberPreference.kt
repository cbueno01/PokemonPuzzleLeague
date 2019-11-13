package com.productions.gizzmoo.pokemonpuzzleleague.settings

import android.content.Context
import android.os.Parcelable
import android.preference.DialogPreference
import android.util.AttributeSet
import android.view.View
import android.widget.NumberPicker
import com.productions.gizzmoo.pokemonpuzzleleague.R

class NumberPreference(context: Context, attributeSet: AttributeSet? = null) : DialogPreference(context, attributeSet) {
    private var value = DEFAULT_VALUE
    private lateinit var numberPicker: NumberPicker

    init {
        dialogLayoutResource = R.layout.preference_number_selector
        setPositiveButtonText(android.R.string.ok)
        setNegativeButtonText(android.R.string.cancel)
    }

    override fun onBindDialogView(view: View) {
        numberPicker = view.findViewById(R.id.number_picker)
        numberPicker.wrapSelectorWheel = false
        numberPicker.maxValue = 50
        numberPicker.minValue = 1
        numberPicker.value = value
        super.onBindDialogView(view)
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        if (restorePersistedValue) {
            value = getPersistedInt(DEFAULT_VALUE)
        } else {
            persistInt(value)
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            value = numberPicker.value

            persistInt(value)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        if (isPersistent) {
            return superState
        }

        val myState = SavedState(superState)
        myState.value = value
        return myState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state == null || state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }

        val myState = state as SavedState?
        super.onRestoreInstanceState(myState?.superState)

        value = myState?.value ?: DEFAULT_VALUE
    }

    companion object {
        const val DEFAULT_VALUE = 10
    }
}