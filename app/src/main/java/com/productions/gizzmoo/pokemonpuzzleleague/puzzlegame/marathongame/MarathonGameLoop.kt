package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.marathongame

import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.Block
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameLoop
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameStatus
import java.util.*

class MarathonGameLoop(grid: Array<Array<Block>>, gameSpeedLevelParam: Int) : GameLoop<MarathonGameLoopListener>(grid) {
    private val rand: Random = Random()
    var newRow: Array<Block> = createNewRowBlocks(rand)
        private set
//    var numOfLinesLeft = numOfLines
//        private set
    var gameSpeedLevel = gameSpeedLevelParam
        private set
    private var numOfFramesToStall: Int = 0
    var linesToNewLevel: Int = getNumOfRowsForLevel()
        private set
    var currentFrameCount: Int = 0
        private set
    var blockMatchAnimating: Int = 0
        private set
    var framesInWarning: Int = 0
        private set

    override fun checkIfGameEnded() {
        checkIfUserLost()
        checkIfUserWon()
    }

    override fun postGameMechanicHook() {
        if (canAnimateUp()) {
            listener?.startAnimatingUp()

            if (currentFrameCount >= getNumOfFramesForCurrentLevel()) {
                addNewRow()
            } else {
                currentFrameCount++
            }

            numOfFramesToStall = 0
        } else {
            numOfFramesToStall--
        }
    }

    override fun notifyBlocksMatched() {
        blockMatchAnimating += blockMatch.size

        if (comboCount > 0) {
            numOfFramesToStall = MAX_FPS * 4
        } else if (blockMatch.size >= 5) {
            numOfFramesToStall = (MAX_FPS * 1.5).toInt()
        } else if (blockMatch.size == 4) {
            numOfFramesToStall = MAX_FPS
        }
        listener?.stopAnimatingUp()
    }

    override fun onProgressUpdate(vararg values: Void?) {
        super.onProgressUpdate()
        val numOfDelayedSeconds = Math.ceil(numOfFramesToStall.toDouble() / MAX_FPS).toInt()
        listener?.updateGameTimeAndSpeed(elapsedTime, gameSpeedLevel, numOfDelayedSeconds)
    }

    fun addNewRow() {
        if (doesRowContainBlock(0)) {
            changeGameStatus(GameStatus.Stopped)
            return
        }

        lock.lock()
        for (i in 1 until NUM_OF_ROWS) {
            for (j in 0 until NUM_OF_COLS) {
                swapBlocks(j, i, j, i - 1)
            }
        }

        for (i in 0 until NUM_OF_COLS) {
            grid[NUM_OF_ROWS - 1][i] = newRow[i]
        }

//        numOfLinesLeft--
        currentFrameCount = 0

        if (!blockSwitcher.isAtTop || doesRowContainBlock(0)) {
            blockSwitcher.moveUp()
        }

        increaseGameSpeedIfNeeded()
        newRow = createNewRowBlocks(rand)
        listener?.newBlockWasAdded()
        lock.unlock()
    }

    fun getNumOfFramesForCurrentLevel(): Int {
        val maxSpeed = 50f
        val minSpeed = 1f

        if (gameSpeedLevel > 50) {
            gameSpeedLevel = 50
        } else if (gameSpeedLevel < 1) {
            gameSpeedLevel = 1
        }

        val normalizedSpeed = (gameSpeedLevel - minSpeed) / (maxSpeed - minSpeed)
        val invertNormSpeed = 1 - normalizedSpeed
        return ((invertNormSpeed * 9 + 1) * 30).toInt()
    }

    private fun checkIfUserLost() {
        when {
            framesInWarning >= getNumOfFramesForCurrentLevel() -> changeGameStatus(GameStatus.Stopped)
            status === GameStatus.Warning -> framesInWarning++
            else -> framesInWarning = 0
        }
    }

    private fun checkIfUserWon() {
//        if (numOfLinesLeft > 11 || isBoardAnimating()) {
//            return
//        }
//
//        for (i in 0 until numOfLinesLeft) {
//            for (j in 0 until NUM_OF_COLS) {
//                if (!grid[i][j].isBlockEmpty) {
//                    return
//                }
//            }
//        }
//
//        didWin = true
//        changeGameStatus(GameStatus.Stopped)
    }

    private fun increaseGameSpeedIfNeeded() {
        if (gameSpeedLevel < 50) {
            if (linesToNewLevel <= 0) {
                gameSpeedLevel++
                linesToNewLevel = getNumOfRowsForLevel()
            } else {
                linesToNewLevel--
            }
        }
    }

    fun aBlockFinishedAnimating() {
        blockMatchAnimating--
    }

    fun setMarathonGameProperties(newRowParam: Array<Block>, numOfBlocksAnimating: Int, linesUntilSpeedIncrease: Int, frameCount: Int, frameCountInWarning: Int) {
        newRow = newRowParam
        blockMatchAnimating = numOfBlocksAnimating
        linesToNewLevel = if (linesUntilSpeedIncrease < 0) getNumOfRowsForLevel() else linesUntilSpeedIncrease
        currentFrameCount = frameCount
        framesInWarning = frameCountInWarning
    }

    private fun getNumOfRowsForLevel(): Int = (gameSpeedLevel * 1.25).toInt() + 3

    fun canAnimateUp(): Boolean = blockMatchAnimating <= 0 && numOfFramesToStall <= 0

    companion object {
        fun createNewRowBlocks(rng: Random) : Array<Block> = Array(NUM_OF_COLS) { i -> Block(rng.nextInt(7) + 1, i, NUM_OF_ROWS - 1) }
    }
}