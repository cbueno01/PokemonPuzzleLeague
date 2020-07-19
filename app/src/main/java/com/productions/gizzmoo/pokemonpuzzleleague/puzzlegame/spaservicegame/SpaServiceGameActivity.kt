package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.spaservicegame

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.productions.gizzmoo.pokemonpuzzleleague.R
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameEndingDialogListener
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.risinggame.RisingGameActivity
import java.util.*

class SpaServiceGameActivity : RisingGameActivity(), GameEndingDialogListener {
    private lateinit var timeView: TextView
    private lateinit var speedView: TextView
    private lateinit var speedGroup: LinearLayout
    private lateinit var stallTimeView: TextView
    private var currentStage = SpaServiceUtils.DEFAULT_STAGE
    private var currentRound = SpaServiceUtils.DEFAULT_ROUND
    lateinit var currentGameProperties: SpaServiceUtils.StageAndRoundProperties

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentStage = intent.getIntExtra(SpaServiceUtils.STAGE_KEY, SpaServiceUtils.DEFAULT_STAGE)
        currentRound = intent.getIntExtra(SpaServiceUtils.ROUND_KEY, SpaServiceUtils.DEFAULT_ROUND)
        currentGameProperties = SpaServiceUtils.getStageAndRoundProperties(currentStage, currentRound)

        setContentView(R.layout.spa_service_game)
        gameFragment = supportFragmentManager.findFragmentById(R.id.puzzleGame) as SpaServiceGameFragment
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

    override fun onGameEndingDialogResponse(didWin: Boolean) {
        if (didWin) {
            SpaServiceUtils.completedRoundInStage(this, currentStage, currentRound)
        }

        finish()
    }
}