package com.productions.gizzmoo.pokemonpuzzleleague.settings

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference

class TextPreference(context: Context, attributeSet: AttributeSet? = null) : DialogPreference(context, attributeSet) {
    override fun getPositiveButtonText(): CharSequence {
        return "Yes"
    }

    override fun getNegativeButtonText(): CharSequence {
        return "No"
    }
}