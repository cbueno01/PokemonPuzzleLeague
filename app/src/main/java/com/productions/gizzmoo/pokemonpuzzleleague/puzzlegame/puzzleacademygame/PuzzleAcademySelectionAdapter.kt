package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.puzzleacademygame

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.productions.gizzmoo.pokemonpuzzleleague.R

class PuzzleAcademySelectionAdapter(private val context: Context, private val onClick: (Int) -> Unit) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return PuzzleAcademyGridViewItem(context).apply {
            text = (position + 1).toString()
            gravity = Gravity.CENTER

            if (FileManager.getJSONReaderWriter(context, 1).didWinLevel(position)) {
                setBackgroundColor(context.resources.getColor(R.color.secondary_light))
                setTextColor(context.resources.getColor(R.color.secondary_text_color))
            } else {
                setBackgroundColor(context.resources.getColor(R.color.primary_dark))
                setTextColor(context.resources.getColor(R.color.primary_text_color))
            }
            setOnClickListener { onClick(position) }
        }
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return FileManager.getJSONReaderWriter(context, 1).getNumOfLevels()
    }

    companion object {
        const val PUZZLE_ID_KEY = "PUZZLE_ID_KEY"
    }
}