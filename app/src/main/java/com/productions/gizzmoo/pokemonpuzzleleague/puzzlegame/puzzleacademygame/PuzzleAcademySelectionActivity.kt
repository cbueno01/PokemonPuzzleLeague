package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.puzzleacademygame

import android.content.Intent
import android.os.Bundle
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import com.productions.gizzmoo.pokemonpuzzleleague.R

class PuzzleAcademySelectionActivity : AppCompatActivity() {
    private lateinit var puzzleAdapter: PuzzleAcademySelectionAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.puzzle_academy_selection)
        val gridView: GridView = findViewById(R.id.gridView)
        puzzleAdapter = PuzzleAcademySelectionAdapter(this) { position ->
            val intent = Intent(this, PuzzleAcademyGameActivity::class.java).apply {
                putExtra(PuzzleAcademySelectionAdapter.PUZZLE_ID_KEY, position)
            }
            this.startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        gridView.adapter = puzzleAdapter
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onStop() {
        super.onStop()
        puzzleAdapter.notifyDataSetChanged()
    }
}