package com.productions.gizzmoo.pokemonpuzzleleague

import android.content.Context

object ViewUtils {
    fun pxFromDp(context: Context, dp: Int): Float =
        dp * context.resources.displayMetrics.density
}