package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.marathongame

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.productions.gizzmoo.pokemonpuzzleleague.R
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameEndingDialogListener
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.risinggame.RisingGameActivity
import java.util.*

/**
 * Created by Chrystian on 1/23/2018.
 */

class MarathonActivity : RisingGameActivity(), GameEndingDialogListener {
    private lateinit var timeView: TextView
    private lateinit var speedView: TextView
    private lateinit var speedGroup: LinearLayout
    private lateinit var stallTimeView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.marathon_game)
        gameFragment = supportFragmentManager.findFragmentById(R.id.puzzleGame) as MarathonGameFragment
        timeView = findViewById(R.id.timerValue)
        speedView = findViewById(R.id.speedValue)
        speedGroup = findViewById(R.id.speedGroup)
        stallTimeView = findViewById(R.id.stallTime)
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

    override fun onGameEndingDialogResponse(didWin: Boolean) { finish() }
}
