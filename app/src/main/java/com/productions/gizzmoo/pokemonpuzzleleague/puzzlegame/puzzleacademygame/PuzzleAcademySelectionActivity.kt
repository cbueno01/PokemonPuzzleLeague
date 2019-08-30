package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.puzzleacademygame

import android.os.Bundle
import android.widget.GridView
import com.productions.gizzmoo.pokemonpuzzleleague.PokemonPuzzleLeagueActivity
import com.productions.gizzmoo.pokemonpuzzleleague.R

class PuzzleAcademySelectionActivity : PokemonPuzzleLeagueActivity() {
    private lateinit var puzzleAdapter: PuzzleAcademySelectionAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.puzzle_academy_selection)
        val gridView = findViewById<GridView>(R.id.gridView)
        puzzleAdapter = PuzzleAcademySelectionAdapter(this)
        gridView.adapter = puzzleAdapter
    }

    override fun onStop() {
        super.onStop()
        puzzleAdapter.notifyDataSetChanged()
    }
}