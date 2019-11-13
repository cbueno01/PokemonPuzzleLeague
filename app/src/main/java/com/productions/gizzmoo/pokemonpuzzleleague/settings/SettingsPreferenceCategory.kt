package com.productions.gizzmoo.pokemonpuzzleleague.settings

import android.content.Context
import android.preference.PreferenceCategory
import android.util.AttributeSet
import android.widget.TextView
import android.view.View
import com.productions.gizzmoo.pokemonpuzzleleague.R


class CustomPreferenceCategory(context: Context, attrs: AttributeSet) : PreferenceCategory(context, attrs) {
    override fun onBindView(view: View) {
        super.onBindView(view)
        val titleView = view.findViewById(android.R.id.title) as TextView
        titleView.setTextColor(context.resources.getColor(R.color.secondary_dark))
    }
}