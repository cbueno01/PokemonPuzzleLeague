package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.marathongame

import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.Block
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameLoop
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameStatus
import java.util.*

class MarathonGameLoop(grid: Array<Array<Block>>, gameSpeedLevelParam: Int) : GameLoop<MarathonGameLoopListener>(grid) {
    private val rand: Random = Random()
    var newRow: Array<Block> = createEmptyBlocksRow()
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
    private var hasUpdatedRisingAnimationStatus = true

    override fun checkIfGameEnded() {
        checkIfUserLost()
        checkIfUserWon()
    }

    override fun postGameMechanicHook() {
        if (canAnimateUp()) {
            if (!hasUpdatedRisingAnimationStatus) {
                listener?.tryToStartAnimatingUp()
                hasUpdatedRisingAnimationStatus = true
            }

            if (currentFrameCount >= getNumOfFramesForCurrentLevel()) {
                addNewRow()
            } else {
                incrementCurrentFrameCount()
            }

            setNumOfFramesToStall(0)
        } else {
            subtractFrameToStall()
            hasUpdatedRisingAnimationStatus = false
        }
    }

    override fun notifyBlocksMatched() {
        val comboCount = getComboCountMax(blockMatch)
        addToBlockMatch(blockMatch.size)

        if (comboCount > 0) {
            setNumOfFramesToStall(MAX_FPS * 4)
        } else if (blockMatch.size >= 5) {
            setNumOfFramesToStall((MAX_FPS * 1.5).toInt())
        } else if (blockMatch.size == 4) {
            setNumOfFramesToStall(MAX_FPS)
        }
        listener?.stopAnimatingUp()
    }

    override fun onProgressUpdate(vararg values: Void?) {
        super.onProgressUpdate()
        val numOfDelayedSeconds = Math.ceil(numOfFramesToStall.toDouble() / MAX_FPS).toInt()
        listener?.updateGameTimeAndSpeed(elapsedTime, gameSpeedLevel, numOfDelayedSeconds)
    }

    override fun onPreExecute() {
        super.onPreExecute()
        listener?.gameIsPrepared()
    }

    override fun getUpdatedGameStatus() {
        if (didWin) {
            changeGameStatus(GameStatus.Stopped)
        } else if (doesRowContainBlock(0)) {
            if (status != GameStatus.Warning) {
                changeGameStatus(GameStatus.Warning)
            }
        } else if (doesRowContainBlock(1)) {
            if (status != GameStatus.InDanger) {
                changeGameStatus(GameStatus.InDanger)
            }
        } else if (doesRowContainBlock(3)) {
            if (status != GameStatus.Panic) {
                changeGameStatus(GameStatus.Panic)
            }
        } else {
            if (status != GameStatus.Running) {
                changeGameStatus(GameStatus.Running)
            }
        }
    }

    @Synchronized
    fun addNewRow() {
        if (doesRowContainBlock(0)) {
            changeGameStatus(GameStatus.Stopped)
            return
        }

        gridLock.lock()
        for (i in 1 until NUM_OF_ROWS) {
            for (j in 0 until NUM_OF_COLS) {
                swapBlocksInternal(j, i, j, i - 1)
            }
        }
        gridLock.unlock()

        for (i in 0 until NUM_OF_COLS) {
            grid[NUM_OF_ROWS - 1][i] = newRow[i]
        }

//        numOfLinesLeft--
        resetCurrentFrameCount()

        if (!blockSwitcher.isAtTop || doesRowContainBlock(0)) {
            blockSwitcher.moveUp()
        }

        increaseGameSpeedIfNeeded()
        newRow = createNewRowBlocks(rand, shouldShowDiamonds(gameSpeedLevel))
        listener?.newBlockWasAdded()
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
        return ((invertNormSpeed * 5 + 1) * 30).toInt()
    }

    @Synchronized
    private fun checkIfUserLost() {
        when {
            framesInWarning >= getNumOfFramesForCurrentLevel() -> changeGameStatus(GameStatus.Stopped)
            status === GameStatus.Warning -> if (canAnimateUp()) { framesInWarning++ }
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

    @Synchronized
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

    @Synchronized
    fun aBlockFinishedAnimating() {
        blockMatchAnimating--
    }

    @Synchronized
    fun resetCurrentFrameCount() {
        currentFrameCount = 0
    }

    fun setMarathonGameProperties(newRowParam: Array<Block>, numOfBlocksAnimating: Int, linesUntilSpeedIncrease: Int, frameCount: Int, frameCountInWarning: Int) {
        newRow = newRowParam
        blockMatchAnimating = numOfBlocksAnimating
        linesToNewLevel = if (linesUntilSpeedIncrease < 0) getNumOfRowsForLevel() else linesUntilSpeedIncrease
        currentFrameCount = frameCount
        framesInWarning = frameCountInWarning
    }

    fun canAnimateUp(): Boolean = blockMatchAnimating <= 0 && numOfFramesToStall <= 0

    private fun getNumOfRowsForLevel(): Int = (gameSpeedLevel * 1.25).toInt() + 3

    @Synchronized
    private fun addToBlockMatch(num: Int) {
        blockMatchAnimating += num
    }

    @Synchronized
    private fun setNumOfFramesToStall(num: Int) {
        numOfFramesToStall = num
    }

    @Synchronized
    private fun subtractFrameToStall() {
        numOfFramesToStall--
    }

    @Synchronized
    private fun incrementCurrentFrameCount() {
        currentFrameCount++
    }

    companion object {
        fun createEmptyBlocksRow() : Array<Block> = Array(NUM_OF_COLS) { i -> Block(0, i, NUM_OF_ROWS - 2) }

        fun createNewRowBlocks(rng: Random, includeDiamonds: Boolean) : Array<Block> = Array(NUM_OF_COLS) { i -> getRandomBlock(rng, i, NUM_OF_ROWS - 1, includeDiamonds) }

        fun getRandomBlock(rng: Random, x: Int, y: Int, includeDiamonds: Boolean): Block {
            val numOfBlockType = if (includeDiamonds) 6 else 5
            return Block(rng.nextInt(numOfBlockType) + 1, x, y)
        }

        fun shouldShowDiamonds(currentLevel: Int): Boolean =
            currentLevel >= 25
    }
}