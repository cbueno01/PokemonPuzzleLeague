package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.marathongame

import android.content.Context
import android.graphics.Canvas
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.BoardResources
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameStatus
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.PuzzleBoardView
import java.util.*

class MarathonPuzzleBoardView(context: Context) : PuzzleBoardView(context) {
    var newRowBlocks = MarathonGameLoop.createEmptyBlocksRow()
    var risingAnimationCounter: Int = 0
    private var risingAnimationOffset: Int = 0
    private var shouldAnimatingUp: Boolean = false
    private var numOfTotalFrames: Int = 1
//    private var winLine: Int = 0
//    private var shouldShowWinLine: Boolean = false

    fun resetRisingAnimationCount() {
        risingAnimationCounter = 1
    }

    fun startAnimatingUp() {
        shouldAnimatingUp = true
    }

    fun stopAnimatingUp() {
        shouldAnimatingUp = false
    }

    fun setGameSpeed(numOfFrames: Int) {
        numOfTotalFrames = numOfFrames
    }

    override fun getSubclassHeightOffset(): Int = risingAnimationOffset

    override fun drawAfterBackground() {
        updateRiseAnimationCountIfNeeded()
    }

    override fun drawAfterGrid(canvas: Canvas) {
        drawNewRow(canvas)
//      drawLine(canvas)
    }

    private fun drawNewRow(canvas: Canvas) {
        val y = 12 * blockSize + heightOffset
        val bitmapRation = risingAnimationOffset.toFloat() / blockSize

        for (i in newRowBlocks.indices) {
            val x = i * blockSize + widthOffset
            blockRect.set(x, y - risingAnimationOffset, x + blockSize, y)
            drawBlock(canvas, newRowBlocks[i], blockRect, bitmapRation)
        }
    }

//    fun winLineAt(line: Int) {
//        if (line <= 12) {
//            shouldShowWinLine = true
//            winLine = line
//        }
//    }

//    private fun drawLine(canvas: Canvas) {
//        if (shouldShowWinLine) {
//            val y = if (winLine != 0 && doesStatusAllowAnimation) winLine * blockSize - risingAnimationOffset else winLine * blockSize
//            canvas.drawLine(widthOffset.toFloat(), (y + heightOffset).toFloat(), (boardWidth + widthOffset).toFloat(), (y + heightOffset).toFloat(), blockSwitcherPaint)
//        }
//    }

    private fun updateRiseAnimationCountIfNeeded() {
        val bitmapBlockSize = (BoardResources.getBlockHeights() * (risingAnimationCounter.toFloat() / numOfTotalFrames)).toInt()
        risingAnimationOffset = (blockSize * (bitmapBlockSize / BoardResources.getBlockHeights().toFloat())).toInt()
        if (shouldAnimatingUp) {
            risingAnimationCounter++
        }
    }
}