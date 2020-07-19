package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.spaservicegame

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.productions.gizzmoo.pokemonpuzzleleague.R
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameCountDownTimer

class SpaServiceStageActivity : Activity() {
    private lateinit var stageTextView: TextView
    private lateinit var roundTextView: TextView
    private var stage = SpaServiceUtils.DEFAULT_STAGE
    private var round = SpaServiceUtils.DEFAULT_ROUND

    private var gameCountDownTimer: GameCountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.spa_service_stage)
        stageTextView = findViewById(R.id.stageTextView)
        roundTextView = findViewById(R.id.roundTextView)

        stage = SpaServiceUtils.getCurrentStage(this)
        round = SpaServiceUtils.getCurrentRound(this)

        stageTextView.text = getString(R.string.stage_text, stage + 1)
        roundTextView.text = getString(R.string.round_text, round + 1)
    }

    override fun onStart() {
        super.onStart()
        gameCountDownTimer = GameCountDownTimer(onFinishTimer(), onTimerUpdate())
        gameCountDownTimer?.start()
    }

    override fun onStop() {
        super.onStop()
        gameCountDownTimer?.cancel()
    }

    private fun onFinishTimer(): () -> Unit {
        return {
            val intent = Intent(this@SpaServiceStageActivity, SpaServiceGameActivity::class.java).apply {
                putExtra(SpaServiceUtils.STAGE_KEY, stage)
                putExtra(SpaServiceUtils.ROUND_KEY, round)
            }
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun onTimerUpdate(): (Int) -> Unit = {}
}