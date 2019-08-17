package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.puzzleacademygame

import android.os.Bundle
import android.widget.TextView
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameActivity
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameDialogFragment
import com.productions.gizzmoo.pokemonpuzzleleague.R
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.puzzleacademygame.PuzzleAcademySelectionAdapter.Companion.PUZZLE_ID_KEY
import java.util.Locale

class PuzzleAcademyGameActivity : GameActivity(), PuzzleAcademyFragment.PuzzleAcademyFragmentInterface, GameDialogFragment.OnGameEndingDialogFragmentReturnListener {
    private lateinit var timeView: TextView
    private lateinit var swapsView: TextView
    private lateinit var gameFragment: PuzzleAcademyFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.puzzle_academy_game)
        timeView = findViewById(R.id.timerValue)
        swapsView = findViewById(R.id.movesLeftValue)
        gameFragment = fragmentManager.findFragmentById(R.id.puzzleBoard) as PuzzleAcademyFragment
        gameFragment.listener = this
        gameFragment.puzzleId = intent.getIntExtra(PUZZLE_ID_KEY, 0)
    }

    override fun updateGameTime(timeInMilli: Long) {
        timeView.text = String.format(Locale.US, "%04d", (timeInMilli / 1000).toInt())
    }

    override fun updateNumOfSwaps(swapsLeft: Int) {
        swapsView.text = swapsLeft.toString()
    }

    override fun onGameEndingDialogResponse() {
        finish()
    }

    override fun shouldPlayPanicMusic(): Boolean {
        return false
    }
}
