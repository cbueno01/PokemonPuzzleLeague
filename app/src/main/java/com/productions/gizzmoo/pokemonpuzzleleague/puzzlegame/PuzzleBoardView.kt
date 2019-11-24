package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.preference.PreferenceManager
import androidx.core.view.MotionEventCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.INVALID_POINTER_ID
import android.view.View
import com.productions.gizzmoo.pokemonpuzzleleague.PokemonResources
import com.productions.gizzmoo.pokemonpuzzleleague.R
import com.productions.gizzmoo.pokemonpuzzleleague.Trainer
import com.productions.gizzmoo.pokemonpuzzleleague.TrainerResources
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.timezonegame.TimeZoneGameLoop
import com.productions.gizzmoo.pokemonpuzzleleague.settings.PokemonPreference
import com.productions.gizzmoo.pokemonpuzzleleague.settings.TrainerPreference
import java.util.*

class PuzzleBoardView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var blocks: Array<Array<Block>>? = null
    private var blockSwitcher: SwitchBlocks? = null

    private var boardWidth: Int = 0
    private var boardHeight: Int = 0
    private var widthOffset: Int = 0
    private var heightOffset: Int = 0
    private var blockSize: Int = 0

    private val blockRect = Rect()
    private val blockRectScale = Rect()
    private val boardRect = Rect()
    private val pokemonBackgroundRect = Rect()

    var newRowBlocks = TimeZoneGameLoop.createNewRowBlocks(Random())
    private var boardPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.FILL_AND_STROKE
        alpha = 180 // 70%
    }
    private var boardBoarderPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }
    private var blockSwitcherPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 7f
    }

    private var trainerBitmap: Bitmap
    private var pokemonBitmap: Bitmap? = null
    private var numOfTotalFrames: Int = 1

    private var activePointerId = INVALID_POINTER_ID
    private var lastTouch: Point? = null
    private var lastTouchPointer: Point? = null
    private var leftBlockSwitcherIsBeingMoved: Boolean = false
    private var rightBlockSwitcherIsBeingMoved: Boolean = false
    var listener: IBoard? = null

    var risingAnimationCounter: Int = 1
    private var risingAnimationOffset: Int = 0
    private var shouldAnimatingUp: Boolean = false
    var showNewBlocks: Boolean = false

    private var currentStatus: GameStatus = GameStatus.Running
    private var winLine: Int = 0
    private var shouldShowWinLine: Boolean = false

    init {
        BoardResources.createImageBitmaps(context)
        val settings = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

        val currentTrainer = Trainer.getTypeByID(settings.getInt("pref_trainer_key", TrainerPreference.DEFAULT_ID))
        val pokemonIndex = settings.getInt("pref_pokemon_key", PokemonPreference.DEFAULT_ID)
        val currentPokemon = PokemonResources.getPokemonForTrainer(currentTrainer)[pokemonIndex]

        val trainerResource = TrainerResources.getTrainerFullBody(currentTrainer, PokemonResources.isEvolvedGary(currentPokemon))
        val pokemonResource = PokemonResources.getPokemonBackground(currentTrainer, currentPokemon)

        trainerBitmap = BitmapFactory.decodeResource(context.resources, trainerResource)
        if (currentTrainer !== Trainer.MEWTWO) {
            pokemonBitmap = BitmapFactory.decodeResource(context.resources, pokemonResource)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        boardWidth = measuredWidth - measuredWidth % 6
        boardHeight = measuredHeight - measuredHeight % 12
        widthOffset = 0
        heightOffset = 0

        blockSize = Math.min(boardWidth / 6, boardHeight / 12)
        if (blockSize == boardWidth / 6) {
            heightOffset = (boardHeight - blockSize * 12) / 2
            boardHeight = blockSize * 12
        } else {
            widthOffset = (boardWidth - blockSize * 6) / 2
            boardWidth = blockSize * 6
        }

        if (heightOffset < 0 || widthOffset < 0) {
            throw RuntimeException("Offsets are negative!")
        }

        heightOffset += 8
        widthOffset += 8

        boardRect.set(widthOffset, heightOffset, boardWidth + widthOffset, boardHeight + heightOffset)

        pokemonBitmap?.let {
            val heightRatio = it.height / trainerBitmap.height.toDouble()
            pokemonBackgroundRect.set(widthOffset, heightOffset, boardWidth + widthOffset, (boardHeight * heightRatio).toInt() + heightOffset)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawBackground(canvas)
        updateRiseAnimationCountIfNeeded()
        drawGrid(canvas)
        drawNewRow(canvas)
        drawBlockSwitcher(canvas)
        drawLine(canvas)
        drawRectBoarder(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (MotionEventCompat.getActionMasked(event)) {
            MotionEvent.ACTION_DOWN -> performActionDown(event)
            MotionEvent.ACTION_MOVE -> performActionMove(event)
            MotionEvent.ACTION_UP -> performActionUp(event)
            MotionEvent.ACTION_CANCEL -> performActionCancel()
            MotionEvent.ACTION_POINTER_UP -> performActionPointerUp(event)
            MotionEvent.ACTION_POINTER_DOWN -> performActionPointerDown(event)
        }
        return true
    }

    private fun setGrid(grid: Array<Array<Block>>) {
        blocks = grid
        invalidate()
        requestLayout()
    }

    fun setGrid(grid: Array<Array<Block>>, switcher: SwitchBlocks) {
        blockSwitcher = switcher
        setGrid(grid)
    }

    fun setGameSpeed(numOfFrames: Int) {
        numOfTotalFrames = numOfFrames
    }

    fun resetRisingAnimationCount() {
        risingAnimationCounter = 1
    }

    fun startAnimatingUp() {
        shouldAnimatingUp = true
    }

    fun stopAnimatingUp() {
        shouldAnimatingUp = false
    }

    fun statusChanged(status: GameStatus) {
        currentStatus = status
    }

    fun winLineAt(line: Int) {
        if (line <= 12) {
            shouldShowWinLine = true
            winLine = line
        }
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.drawRect(boardRect, boardPaint)
        canvas.drawBitmap(trainerBitmap, null, boardRect, null)
        pokemonBitmap?.let {
            canvas.drawBitmap(it, null, pokemonBackgroundRect, null)
        }
    }

    private fun drawGrid(canvas: Canvas) {
        blocks?.let {
            for (row in it.indices.reversed()) {
                for (blockIndex in 0 until it[row].size) {
                    val widthStartPosition = blockIndex * blockSize + widthOffset
                    val heightStartPosition = row * blockSize + heightOffset - (if (doesStatusAllowAnimation && showNewBlocks) risingAnimationOffset else 0)
                    handleDrawingBlock(canvas, it[row][blockIndex], widthStartPosition, heightStartPosition, row, blockIndex)
                }
            }
        }
    }

    private fun handleDrawingBlock(canvas: Canvas, block: Block, widthStartPosition: Int, heightStartPosition: Int, row: Int, blockIndex: Int) {
        if (!block.isBlockEmpty) {
            when {
                block.hasMatched -> drawMatchedBlock(canvas, block, widthStartPosition, heightStartPosition, row, blockIndex)
                block.isBeingSwitched -> drawSwitchingBlock(canvas, block, widthStartPosition, heightStartPosition)
                block.isAnimatingDown -> drawFallingBlock(canvas, block, widthStartPosition, heightStartPosition, row, blockIndex)
                else -> removeComboAndDrawBlock(canvas, block, widthStartPosition, heightStartPosition)
            }
        } else {
            // Handle blanks that are being switched
            if (block.isBeingSwitched) {
                block.incrementSwitchAnimationFrame()

                if (block.switchAnimationCount >= ANIMATION_SWITCH_FRAMES_NEEDED) {
                    block.clear()
                }
            }
        }
    }

    private fun drawMatchedBlock(canvas: Canvas, block: Block, widthStartPosition: Int, heightStartPosition: Int, row: Int, blockIndex: Int) {
        blockRect.set(widthStartPosition, heightStartPosition, widthStartPosition + blockSize, heightStartPosition + blockSize)

        if (block.matchInvertedAnimationCount > 0) {
            drawMatchedBlockHelper(canvas, block, blockRect, true, 0)
            block.decrementInvertedAnimationFrame()
        } else if (block.delayMatchAnimationCount > 0) {
            drawMatchedBlockHelper(canvas, block, blockRect, false, 0)
            block.decrementDelayedMatchAnimationFrame()
        } else if (block.matchPopAnimationCount < ANIMATION_MATCH_POP_FRAMES_NEEDED) {
            drawMatchedBlockHelper(canvas, block, blockRect, false, block.matchPopAnimationCount)
            block.incrementPopAnimationFrame()
        } else if (block.clearMatchCount > 0) {
            drawMatchedBlockHelper(canvas, block, blockRect, false, ANIMATION_MATCH_POP_FRAMES_NEEDED + 1)
            block.decrementClearFrame()
            if (!block.hasPopped) {
                listener?.blockIsPopping(block.popPosition, block.matchTotalCount)
                block.blockPopped()
            }
        } else {
            drawMatchedBlockHelper(canvas, block, blockRect, false, ANIMATION_MATCH_POP_FRAMES_NEEDED + 1)
            listener?.blockFinishedMatchAnimation(row, blockIndex)
            block.clear()
        }
    }

    private fun drawMatchedBlockHelper(canvas: Canvas, block: Block, position: Rect, isInverted: Boolean, animationCount: Int) {
        val currentBitmap: Bitmap? = if (isInverted) BoardResources.getInvertedBlock(block.type) else BoardResources.getPopAnimationBlock(block.type, animationCount)
        canvas.drawBitmap(currentBitmap!!, null, position, null)
    }

    private fun drawSwitchingBlock(canvas: Canvas, block: Block, widthStartPosition: Int, heightStartPosition: Int) {
        val switchAnimationOffset = (blockSize - block.switchAnimationCount / ANIMATION_SWITCH_FRAMES_NEEDED.toFloat() * blockSize).toInt()
        if (block.isAnimatingLeft) {
            blockRect.set(widthStartPosition + switchAnimationOffset, heightStartPosition, widthStartPosition + blockSize + switchAnimationOffset, heightStartPosition + blockSize)
            drawBlock(canvas, block, blockRect, 1f)
        } else {
            blockRect.set(widthStartPosition - switchAnimationOffset, heightStartPosition, widthStartPosition + blockSize - switchAnimationOffset, heightStartPosition + blockSize)
            drawBlock(canvas, block, blockRect, 1f)
        }

        block.incrementSwitchAnimationFrame()

        if (block.switchAnimationCount >= ANIMATION_SWITCH_FRAMES_NEEDED) {
            block.stopSwitchAnimation()
        }
    }

    private fun drawFallingBlock(canvas: Canvas, block: Block, widthStartPosition: Int, heightStartPosition: Int, row: Int, blockIndex: Int) {
        val fallingAnimationOffset = ((ANIMATION_FALLING_FRAMES_NEEDED - block.downAnimatingCount) / ANIMATION_FALLING_FRAMES_NEEDED.toFloat() * blockSize).toInt()
        blockRect.set(widthStartPosition, heightStartPosition - fallingAnimationOffset, widthStartPosition + blockSize, heightStartPosition + blockSize - fallingAnimationOffset)
        drawBlock(canvas, block, blockRect, 1f)

        var blockNeedsToSwap = false
        if (block.downAnimatingCount >= ANIMATION_FALLING_FRAMES_NEEDED) {
            if (row < blocks!!.size - 1 && (blocks!![row + 1][blockIndex].isBlockEmpty || blocks!![row + 1][blockIndex].isAnimatingDown) && !(blocks!![row + 1][blockIndex].isBeingSwitched || blocks!![row + 1][blockIndex].hasMatched)) {
                blockNeedsToSwap = true
            } else {
                if (block.canCombo) {
                    block.removeComboFlagOnNextFrame = true
                }
                block.stopFallingAnimation()
            }
        } else {
            block.incrementDownAnimationFrame()
        }

        if (blockNeedsToSwap) {
            block.startFallingAnimation()
            listener?.needsBlockSwap(blockIndex, row, blockIndex, row + 1)
        }
    }

    private fun removeComboAndDrawBlock(canvas: Canvas, block: Block, widthStartPosition: Int, heightStartPosition: Int) {
        if (block.removeComboFlagOnNextFrame) {
            block.removeComboFlagOnNextFrame = false
            block.canCombo = false
        }

        blockRect.set(widthStartPosition, heightStartPosition, widthStartPosition + blockSize, heightStartPosition + blockSize)
        drawBlock(canvas, block, blockRect, 1f)
    }

    private fun drawBlock(canvas: Canvas, block: Block, position: Rect, heightRatio: Float) {
        if (!block.isBlockEmpty) {
            val heightRatioWithGuards = when {
                heightRatio > 1 -> 1f
                heightRatio < 0 -> 0f
                else -> heightRatio
            }

            val currentBitmap: Bitmap? = if (heightRatioWithGuards == 1f) BoardResources.getNormalBlock(block.type) else BoardResources.getDarkBlock(block.type)
            blockRectScale.set(0, 0, currentBitmap!!.width, (currentBitmap.height * heightRatioWithGuards).toInt())
            canvas.drawBitmap(currentBitmap, blockRectScale, position, null)
        }
    }

    private fun drawNewRow(canvas: Canvas) {
        if (showNewBlocks && doesStatusAllowAnimation) {
            val y = 12 * blockSize + heightOffset
            val bitmapRation = risingAnimationOffset.toFloat() / blockSize

            for (i in newRowBlocks.indices) {
                val x = i * blockSize + widthOffset
                blockRect.set(x, y - risingAnimationOffset, x + blockSize, y)
                drawBlock(canvas, newRowBlocks[i], blockRect, bitmapRation)
            }
        }
    }

    private fun drawBlockSwitcher(canvas: Canvas) {
        blockSwitcher?.let {
            val leftBlock = it.leftBlock
            val x = leftBlock.x * blockSize + widthOffset
            var y = leftBlock.y * blockSize + heightOffset

            if (doesStatusAllowAnimation) {
                y -= risingAnimationOffset
            }

            canvas.drawLine(x.toFloat(), y.toFloat(), (x + blockSize / 4).toFloat(), y.toFloat(), blockSwitcherPaint)
            canvas.drawLine(x.toFloat(), y.toFloat(), x.toFloat(), (y + blockSize / 4).toFloat(), blockSwitcherPaint)
            canvas.drawLine(x.toFloat(), (y + 3 * blockSize / 4).toFloat(), x.toFloat(), (y + blockSize).toFloat(), blockSwitcherPaint)
            canvas.drawLine(x.toFloat(), (y + blockSize).toFloat(), (x + blockSize / 4).toFloat(), (y + blockSize).toFloat(), blockSwitcherPaint)

            canvas.drawLine((x + 3 * blockSize / 4).toFloat(), y.toFloat(), (x + 5 * blockSize / 4).toFloat(), y.toFloat(), blockSwitcherPaint)
            canvas.drawLine((x + blockSize).toFloat(), y.toFloat(), (x + blockSize).toFloat(), (y + blockSize / 4).toFloat(), blockSwitcherPaint)
            canvas.drawLine((x + blockSize).toFloat(), (y + 3 * blockSize / 4).toFloat(), (x + blockSize).toFloat(), (y + blockSize).toFloat(), blockSwitcherPaint)
            canvas.drawLine((x + 3 * blockSize / 4).toFloat(), (y + blockSize).toFloat(), (x + 5 * blockSize / 4).toFloat(), (y + blockSize).toFloat(), blockSwitcherPaint)

            canvas.drawLine((x + 7 * blockSize / 4).toFloat(), y.toFloat(), (x + 2 * blockSize).toFloat(), y.toFloat(), blockSwitcherPaint)
            canvas.drawLine((x + 2 * blockSize).toFloat(), y.toFloat(), (x + 2 * blockSize).toFloat(), (y + blockSize / 4).toFloat(), blockSwitcherPaint)
            canvas.drawLine((x + 2 * blockSize).toFloat(), (y + 3 * blockSize / 4).toFloat(), (x + 2 * blockSize).toFloat(), (y + blockSize).toFloat(), blockSwitcherPaint)
            canvas.drawLine((x + 7 * blockSize / 4).toFloat(), (y + blockSize).toFloat(), (x + 2 * blockSize).toFloat(), (y + blockSize).toFloat(), blockSwitcherPaint)
        }
    }

    private fun drawLine(canvas: Canvas) {
        if (shouldShowWinLine) {
            val y = if (winLine != 0 && doesStatusAllowAnimation) winLine * blockSize - risingAnimationOffset else winLine * blockSize
            canvas.drawLine(widthOffset.toFloat(), (y + heightOffset).toFloat(), (boardWidth + widthOffset).toFloat(), (y + heightOffset).toFloat(), blockSwitcherPaint)
        }
    }

    private fun drawRectBoarder(canvas: Canvas) {
        val colors = context.resources.getIntArray(R.array.blue_border_colors)
        val lightColor = colors[0]
        val darkColor = colors[1]
        boardBoarderPaint.strokeWidth = 2f

        for (i in 0..15) {
            val red = ((Color.red(lightColor) - Color.red(darkColor)) * i / 15f).toInt()
            val green = ((Color.green(lightColor) - Color.green(darkColor)) * i / 15f).toInt()
            val blue = ((Color.blue(lightColor) - Color.blue(darkColor)) * i / 15f).toInt()

            var newRed = Color.red(lightColor) - red
            var newGreen = Color.green(lightColor) - green
            var newBlue = Color.blue(lightColor) - blue

            newRed = if (newRed < 0) 0 else newRed
            newGreen = if (newGreen < 0) 0 else newGreen
            newBlue = if (newBlue < 0) 0 else newBlue

            val newColor = Color.rgb(newRed, newGreen, newBlue)

            boardBoarderPaint.color = newColor
            canvas.drawRect((widthOffset + i).toFloat(), (heightOffset + i).toFloat(), (boardWidth + widthOffset - i).toFloat(), (boardHeight + heightOffset - i).toFloat(), boardBoarderPaint)
        }
    }

    private val doesStatusAllowAnimation: Boolean
        get() = currentStatus === GameStatus.Running || currentStatus === GameStatus.Panic

    private fun updateRiseAnimationCountIfNeeded() {
        val bitmapBlockSize = (BoardResources.getBlockHeights() * (risingAnimationCounter.toFloat() / numOfTotalFrames)).toInt()
        risingAnimationOffset = (blockSize * (bitmapBlockSize / BoardResources.getBlockHeights().toFloat())).toInt()
        if (shouldAnimatingUp) {
            risingAnimationCounter++
        }
    }

    private fun getGridCoordinatesOffXY(x: Float, y: Float): Point {
        var posX = Math.abs((x - BOARD_PADDING.toFloat() - widthOffset.toFloat()) / blockSize).toInt()
        var posY = Math.abs((y - BOARD_PADDING.toFloat() - heightOffset.toFloat()) / blockSize).toInt()

        // Don't return a point out of the grid.
        if (posX > 5) {
            posX = 5
        }

        if (posY > 11) {
            posY = 11
        }

        return Point(posX, posY)
    }

    private fun performActionDown(event: MotionEvent) {
        val pointerIndex = MotionEventCompat.getActionIndex(event)
        val x = MotionEventCompat.getX(event, pointerIndex)
        val y = MotionEventCompat.getY(event, pointerIndex)
        val p = getGridCoordinatesOffXY(x, y + risingAnimationOffset)

        // Remember where we started (for dragging)
        lastTouch = p
        // Save the ID of this pointer (for dragging)
        activePointerId = MotionEventCompat.getPointerId(event, 0)
        blockSwitcher?.let {
            if (it.areCoordinatesInSwitcher(p.x, p.y)) {
                it.switcherIsBeingMoved = true
                leftBlockSwitcherIsBeingMoved = it.areCoordinatesInLeftBlock(p.x, p.y)
                rightBlockSwitcherIsBeingMoved = it.areCoordinatesInRightBlock(p.x, p.y)
            }
        }
    }

    private fun performActionMove(event: MotionEvent) {
        // Find the index of the active pointer and fetch its position
        val pointerIndex = MotionEventCompat.findPointerIndex(event, activePointerId)

        val x = MotionEventCompat.getX(event, pointerIndex)
        val y = MotionEventCompat.getY(event, pointerIndex)
        val newP = getGridCoordinatesOffXY(x, y + risingAnimationOffset)

        blockSwitcher?.let {
            if (it.switcherIsBeingMoved) {
                var isReallyMoving = true
                if (leftBlockSwitcherIsBeingMoved && !it.areCoordinatesInLeftBlock(newP.x, newP.y)) {
                    if (newP.x > 4) {
                        newP.x = 4
                        isReallyMoving = false
                    }

                    if ((currentStatus === GameStatus.Running || currentStatus === GameStatus.Panic) && newP.y == 0) {
                        newP.y = 1
                        isReallyMoving = false
                    }

                    it.setLeftBlock(newP)

                    if (isReallyMoving) {
                        listener?.switchBlockMoved()
                    }
                } else if (rightBlockSwitcherIsBeingMoved && !it.areCoordinatesInRightBlock(newP.x, newP.y)) {
                    if (newP.x < 1) {
                        newP.x = 1
                        isReallyMoving = false
                    }

                    if ((currentStatus === GameStatus.Running || currentStatus === GameStatus.Panic) && newP.y == 0) {
                        newP.y = 1
                        isReallyMoving = false
                    }

                    it.setRightBlock(newP)
                    if (isReallyMoving) {
                        listener?.switchBlockMoved()
                    }
                }

                // Remember this touch position for the next move event
                lastTouch = newP
            }
        }
    }

    private fun performActionUp(event: MotionEvent) {
        activePointerId = INVALID_POINTER_ID

        blockSwitcher?.let { blockSwitcher ->
            if (!blockSwitcher.switcherIsBeingMoved) {
                val currentX = event.x
                val currentY = event.y
                val p = getGridCoordinatesOffXY(currentX, currentY + risingAnimationOffset)
                val deltaY = if (lastTouch != null) lastTouch!!.y - p.y else 0

                if (deltaY >= MIN_SWIPE_DISTANCE) {
                    listener?.boardSwipedUp()
                } else if (p.x == lastTouch?.x && p.y == lastTouch?.y) {
                    val leftBlockSwitch = blockSwitcher.leftBlock
                    tryToSwitch(leftBlockSwitch)
                }

            }

            blockSwitcher.switcherIsBeingMoved = false
        }
    }

    private fun performActionCancel() {
        activePointerId = INVALID_POINTER_ID
        blockSwitcher?.switcherIsBeingMoved = false
    }

    private fun performActionPointerUp(event: MotionEvent) {
        val pointerIndex = MotionEventCompat.getActionIndex(event)

        val currentX = MotionEventCompat.getX(event, pointerIndex)
        val currentY = MotionEventCompat.getY(event, pointerIndex)
        val p = getGridCoordinatesOffXY(currentX, currentY + risingAnimationOffset)

        if (p.x == lastTouchPointer?.x && p.y == lastTouchPointer?.y) {
             blockSwitcher?.let {
                 tryToSwitch(it.leftBlock)
             }
        }

        val pointerId = MotionEventCompat.getPointerId(event, pointerIndex)

        if (pointerId == activePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            activePointerId = MotionEventCompat.getPointerId(event, newPointerIndex)
            blockSwitcher?.switcherIsBeingMoved = false
        }
    }

    private fun performActionPointerDown(event: MotionEvent) {
        val pointerIndex = MotionEventCompat.getActionIndex(event)
        val x = MotionEventCompat.getX(event, pointerIndex)
        val y = MotionEventCompat.getY(event, pointerIndex)
        lastTouchPointer = getGridCoordinatesOffXY(x, y + risingAnimationOffset)
    }

    private fun tryToSwitch(leftBlockSwitch: Point) {
        blocks?.let {
            try {
                if (!it[leftBlockSwitch.y - 1][leftBlockSwitch.x].isAnimatingDown && !it[leftBlockSwitch.y - 1][leftBlockSwitch.x + 1].isAnimatingDown && tryToSwitchHelper(leftBlockSwitch, it)) {
                    listener?.switchBlock(leftBlockSwitch)
                }
            } catch (e: Exception) {
                if (tryToSwitchHelper(leftBlockSwitch, it)) {
                    listener?.switchBlock(leftBlockSwitch)
                }
            }
        }

    }

    private fun tryToSwitchHelper(leftBlockSwitch: Point, blocks: Array<Array<Block>>): Boolean {
        if (!blocks[leftBlockSwitch.y][leftBlockSwitch.x].isAnimatingDown && !blocks[leftBlockSwitch.y][leftBlockSwitch.x + 1].isAnimatingDown) {
            if (!blocks[leftBlockSwitch.y][leftBlockSwitch.x].hasMatched && !blocks[leftBlockSwitch.y][leftBlockSwitch.x + 1].hasMatched) {
                return true
            }
        }

        return false
    }

    companion object {
        const val BOARD_PADDING = 16
        const val ANIMATION_SWITCH_FRAMES_NEEDED = 6
        const val ANIMATION_FALLING_FRAMES_NEEDED = 4
        const val ANIMATION_MATCH_POP_FRAMES_NEEDED = 10
        const val ANIMATION_MATCH_INVERT_FRAMES_NEEDED = 6
        const val MIN_SWIPE_DISTANCE = 3
    }
}