package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.puzzleacademygame

import android.content.Context
import android.widget.TextView

class PuzzleAcademyGridViewItem(context: Context) : TextView(context) {
    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec) // This is the key that will make the height equivalent to its width
    }
}