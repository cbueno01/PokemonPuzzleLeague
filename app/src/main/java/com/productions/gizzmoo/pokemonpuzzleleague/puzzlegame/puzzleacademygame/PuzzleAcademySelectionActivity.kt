package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.puzzleacademygame

import android.os.Bundle
import android.widget.GridView
import com.productions.gizzmoo.pokemonpuzzleleague.PokemonPuzzleLeagueActivity
import com.productions.gizzmoo.pokemonpuzzleleague.R
import org.json.JSONObject

class PuzzleAcademySelectionActivity : PokemonPuzzleLeagueActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.puzzle_academy_selection)
        val gridView = findViewById<GridView>(R.id.gridView)
        val json = JSONObject(JSONUtils.getJSONStringFromFile(this))
        val stage = JSONUtils.getStageObjectFromKey(json, 1)
        gridView.adapter = PuzzleAcademySelectionAdapter(this, stage.length())
    }
}