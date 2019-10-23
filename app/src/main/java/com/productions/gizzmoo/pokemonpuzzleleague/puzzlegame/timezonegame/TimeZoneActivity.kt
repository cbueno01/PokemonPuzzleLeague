package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.timezonegame

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.productions.gizzmoo.pokemonpuzzleleague.R
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameActivity
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameEndingDialogListener
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameStatus
import java.util.*

/**
 * Created by Chrystian on 1/23/2018.
 */

class TimeZoneActivity : GameActivity(), GameEndingDialogListener, TimeZoneGameFragment.TimeZoneFragmentInterface {
    private lateinit var timeView: TextView
    private lateinit var speedView: TextView
    private lateinit var speedGroup: LinearLayout
    private lateinit var stallTimeView: TextView
    private lateinit var gameFragment: TimeZoneGameFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.time_zone_game)
        timeView = findViewById(R.id.timerValue)
        speedView = findViewById(R.id.speedValue)
        speedGroup = findViewById(R.id.speedGroup)
        stallTimeView = findViewById(R.id.stallTime)
        gameFragment = fragmentManager.findFragmentById(R.id.puzzleBoard) as TimeZoneGameFragment
        gameFragment.setFragmentListener(this)
    }

    override fun changeSong(isPanic: Boolean) {
        if (!isMusicServiceBound) {
            return
        }

        musicService!!.changeSong(isPanic)
    }

    override fun updateGameTimeAndSpeed(timeInMilli: Long, gameSpeed: Int, delayInSeconds: Int) {
        timeView.text = String.format(Locale.US, "%04d", (timeInMilli / 1000).toInt())
        speedView.text = String.format(Locale.US, "%02d", gameSpeed)
        if (delayInSeconds <= 0) {
            speedGroup.visibility = View.VISIBLE
            stallTimeView.visibility = View.GONE
        } else {
            speedGroup.visibility = View.GONE
            stallTimeView.visibility = View.VISIBLE
            stallTimeView.text = delayInSeconds.toString()
        }
    }

    override fun onGameEndingDialogResponse(didWin: Boolean) {
        finish()
    }

    override fun shouldPlayPanicMusic(): Boolean {
        return if (gameFragment.gameLoop != null) gameFragment.gameLoop.gameStatus != GameStatus.Running else false
    }
}
