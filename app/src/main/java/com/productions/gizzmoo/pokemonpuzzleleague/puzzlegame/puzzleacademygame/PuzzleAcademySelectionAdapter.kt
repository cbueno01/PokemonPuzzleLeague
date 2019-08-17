package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.puzzleacademygame

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.productions.gizzmoo.pokemonpuzzleleague.R

class PuzzleAcademySelectionAdapter(private val context: Context, private val count: Int) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return PuzzleAcademyGridViewItem(context).apply {
            text = (position + 1).toString()
            gravity = Gravity.CENTER
            setBackgroundResource(R.drawable.rectagle_border)
            setOnClickListener {
                val intent = Intent(context, PuzzleAcademyGameActivity::class.java).apply {
                    putExtra(PUZZLE_ID_KEY, position)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return count
    }

    companion object {
        const val PUZZLE_ID_KEY = "PUZZLE_ID_KEY"
    }
}