package com.productions.gizzmoo.pokemonpuzzleleague.settings

import android.content.Context
import android.os.Parcelable
import androidx.preference.DialogPreference
import android.util.AttributeSet

class NumberPreference(context: Context, attributeSet: AttributeSet? = null) : DialogPreference(context, attributeSet) {
    var value = DEFAULT_VALUE
        private set

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        if (restorePersistedValue) {
            value = getPersistedInt(DEFAULT_VALUE)
        } else {
            persistInt(value)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
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

    override fun getSummary(): CharSequence {
        return value.toString()
    }

    fun persistNumber(newValue: Int) {
        value = newValue
        persistInt(newValue)
        summary = newValue.toString()
    }

    companion object {
        const val DEFAULT_VALUE = 10
    }
}