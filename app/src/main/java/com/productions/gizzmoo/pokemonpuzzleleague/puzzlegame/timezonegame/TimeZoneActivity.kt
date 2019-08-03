package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.timezonegame

import android.os.Bundle
import android.widget.TextView
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameActivity
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameDialogFragment
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameStatus
import com.productions.gizzmoo.pokemonpuzzleleague.R
import java.util.Locale

/**
 * Created by Chrystian on 1/23/2018.
 */

class TimeZoneActivity : GameActivity(), GameDialogFragment.OnGameEndingDialogFragmentReturnListener, TimeZoneGameFragment.TimeZoneFragmentInterface {
    private lateinit var timeView: TextView
    private lateinit var speedView: TextView
    private lateinit var gameFragment: TimeZoneGameFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.time_zone_layout)
        timeView = findViewById(R.id.timerValue)
        speedView = findViewById(R.id.speedValue)
        gameFragment = fragmentManager.findFragmentById(R.id.puzzleBoard) as TimeZoneGameFragment
        gameFragment.setFragmentListener(this)
    }

    override fun changeSong(isPanic: Boolean) {
        if (!isMusicServiceBound) {
            return
        }

        musicService!!.changeSong(isPanic)
    }

    override fun updateGameTimeAndSpeed(timeInMilli: Long, gameSpeed: Int) {
        timeView.text = String.format(Locale.US, "%04d", (timeInMilli / 1000).toInt())
        speedView.text = String.format(Locale.US, "%02d", gameSpeed)
    }

    override fun onGameEndingDialogResponse() {
        finish()
    }

    override fun shouldPlayPanicMusic(): Boolean {
        return if (gameFragment.gameLoop != null) gameFragment.gameLoop.gameStatus != GameStatus.Running else false
    }
}
