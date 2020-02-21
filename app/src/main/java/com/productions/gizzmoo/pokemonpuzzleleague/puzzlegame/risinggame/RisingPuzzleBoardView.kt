package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.risinggame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.Block
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.BoardResources
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.PuzzleBoardView

open class RisingPuzzleBoardView(context: Context) : PuzzleBoardView(context) {
    var newRowBlocks = RisingGameLoop.createEmptyBlocksRow()
    private var risingAnimationCounter: Int = 0
    private var risingAnimationOffset: Int = 0
    private var shouldAnimatingUp: Boolean = false
    private var numOfTotalFrames: Int = 1
    private val blockRectScale = Rect()
    private var jumpAnimationCounter: Int = 0
    var isInDanger: Boolean = false
    var isInWarning: Boolean = false
//    private var winLine: Int = 0
//    private var shouldShowWinLine: Boolean = false

    fun resetRisingAnimationCount() {
        setRisingAnimationCounter(1)
    }

    @Synchronized
    fun startAnimatingUp() {
        shouldAnimatingUp = true
    }

    @Synchronized
    fun stopAnimatingUp() {
        shouldAnimatingUp = false
    }

    @Synchronized
    fun setGameSpeed(numOfFrames: Int) {
        numOfTotalFrames = numOfFrames
    }

    override fun getSubclassHeightOffset(): Int = risingAnimationOffset

    override fun drawAfterBackground() {
        updateRiseAnimationCountIfNeeded()
    }

    override fun drawAfterGrid(canvas: Canvas) {
        drawNewRow(canvas)
        updateJumpAnimationCountIfNeeded()
//      drawLine(canvas)
    }

    override fun drawBlock(canvas: Canvas, block: Block, position: Rect) {
        if (!block.isBlockEmpty && !block.isAnimating) {
            when {
                isInDanger -> drawBlockWithBitmap(canvas, BoardResources.getJumpAnimationBlock(block.type, jumpAnimationCounter / JUMP_ANIMATION_MULTIPLIER), null, position)
                isInWarning -> drawBlockWithBitmap(canvas, BoardResources.getSquishedBlock(block.type), null, position)
                else -> super.drawBlock(canvas, block, position)
            }
        } else {
            super.drawBlock(canvas, block, position)
        }
    }

    @Synchronized
    fun setRisingAnimationCounter(num: Int) {
        risingAnimationCounter = num
    }

    @Synchronized
    private fun addToRisingAnimationCounter() {
        risingAnimationCounter++
    }

    private fun drawNewRow(canvas: Canvas) {
        val y = 12 * blockSize + heightOffset
        val bitmapRation = risingAnimationOffset.toFloat() / blockSize

        for (i in newRowBlocks.indices) {
            val x = i * blockSize + widthOffset
            blockRect.set(x, y - risingAnimationOffset, x + blockSize, y)

            val bitmapRationWithGuard = when {
                bitmapRation > 1 -> 1f
                bitmapRation < 0 -> 0f
                else -> bitmapRation
            }

            val currentBitmap = BoardResources.getDarkBlock(newRowBlocks[i].type)
            blockRectScale.set(0, 0, currentBitmap.width, (currentBitmap.height * bitmapRationWithGuard).toInt())
            drawBlockWithBitmap(canvas, currentBitmap, blockRectScale, blockRect)
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
        val risingAnimationRatio = risingAnimationCounter.toFloat() / numOfTotalFrames
        risingAnimationOffset = (blockSize * risingAnimationRatio).toInt()
        if (shouldAnimatingUp) {
            addToRisingAnimationCounter()
        }
    }

    private fun updateJumpAnimationCountIfNeeded() {
        jumpAnimationCounter = if (isInDanger && jumpAnimationCounter < JUMP_ANIMATION_FRAMES) jumpAnimationCounter + 1 else 0
    }

    companion object {
        const val JUMP_ANIMATION_MULTIPLIER = 4
        const val JUMP_ANIMATION_FRAMES = 2 * JUMP_ANIMATION_MULTIPLIER
    }
}