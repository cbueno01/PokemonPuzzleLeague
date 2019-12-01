package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame

import android.os.CountDownTimer

class GameCountDownTimer(private val onFinishCallback: () -> Unit, private val onUpdateCallback: (Int) -> Unit) : CountDownTimer(3000, 1000) {
    override fun onFinish() {
        onFinishCallback()
    }

    override fun onTick(millisUntilFinished: Long) {
        onUpdateCallback((millisUntilFinished / 1000).toInt() + 1)
    }

}