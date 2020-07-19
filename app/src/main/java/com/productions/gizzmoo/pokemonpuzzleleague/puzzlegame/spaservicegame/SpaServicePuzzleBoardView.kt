package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.spaservicegame

import android.content.Context
import android.graphics.Canvas
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.risinggame.RisingPuzzleBoardView

class SpaServicePuzzleBoardView(context: Context) : RisingPuzzleBoardView(context) {
    private var winLine: Int = 0
    private var shouldShowWinLine: Boolean = false

    override fun drawAfterGrid(canvas: Canvas) {
        super.drawAfterGrid(canvas)
        drawLine(canvas)
    }

    fun winLineAt(line: Int) {
        shouldShowWinLine = true
        winLine = line
    }

    private fun drawLine(canvas: Canvas) {
        if (shouldShowWinLine) {
            val y = winLine * blockSize - risingAnimationOffset //- if (winLine != 0 && doesStatusAllowAnimation) risingAnimationOffset else 0
            canvas.drawLine(widthOffset.toFloat(), (y + heightOffset).toFloat(), (boardWidth + widthOffset).toFloat(), (y + heightOffset).toFloat(), blockSwitcherPaint)
        }
    }
}