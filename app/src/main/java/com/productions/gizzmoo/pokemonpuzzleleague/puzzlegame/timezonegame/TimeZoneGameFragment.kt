package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.timezonegame

import android.os.Bundle
import android.preference.PreferenceManager
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.Block
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameDialogFragment
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameFragment
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameLoop.Companion.NUM_OF_COLS
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameLoop.Companion.NUM_OF_ROWS
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameStatus
import java.util.Random

class TimeZoneGameFragment : GameFragment<TimeZoneGameLoop>(), TimeZoneGameLoopListener {
    var listener: TimeZoneFragmentInterface? = null
    private var tempNumOfLinesLeft: Int = 0
    private var tempGameSpeed: Int = 0
    private var tempBlockMatchAnimating: Int = 0
    private var tempLinesUntilSpeedIncrease: Int = -1 // -1 so game loop knows to get the full amount of rows needed
    private var tempCurrentFrameCount: Int = 0
    private var tempFrameCountInWarning: Int = 0
    private var tempNewBlockRow = TimeZoneGameLoop.createNewRowBlocks(Random())
    private var mPrevGameStatus: GameStatus = GameStatus.Stopped

    private var rand: Random = Random()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            tempBlockMatchAnimating = savedInstanceState.getInt(matchAnimationCountKey)
            tempNumOfLinesLeft = savedInstanceState.getInt(linesToWinKey)
            tempGameSpeed = savedInstanceState.getInt(gameSpeedKey)
            tempLinesUntilSpeedIncrease = savedInstanceState.getInt(linesUntilSpeedIncreaseKey)
            tempCurrentFrameCount = savedInstanceState.getInt(frameCountKey)
            tempFrameCountInWarning = savedInstanceState.getInt(frameCountInWarningKey)
            tempNewBlockRow = savedInstanceState.getSerializable(newRowKey) as Array<Block>
        } else {
            val settings = PreferenceManager.getDefaultSharedPreferences(activity!!.applicationContext)
            tempNumOfLinesLeft = settings.getInt("pref_lines_key", 15) + NUM_OF_ROWS
            tempGameSpeed = settings.getInt("pref_speed_key", 10)
        }
    }

    override fun onStop() {
        super.onStop()
        tempNumOfLinesLeft = gameLoop.numOfLinesLeft
        tempGameSpeed = gameLoop.gameSpeedLevel
        tempBlockMatchAnimating = gameLoop.blockMatchAnimating
        tempLinesUntilSpeedIncrease = gameLoop.linesToNewLevel
        tempCurrentFrameCount = gameLoop.currentFrameCount
        tempFrameCountInWarning = gameLoop.framesInWarning
        tempNewBlockRow = gameLoop.newRow
    }

    override fun onStart() {
        super.onStart()
        mPrevGameStatus = gameLoop.status
        gameLoop.setTimeZoneGameProperties(tempNewBlockRow, tempBlockMatchAnimating, tempLinesUntilSpeedIncrease, tempCurrentFrameCount, tempFrameCountInWarning)
        drawLineIfNeeded(tempNumOfLinesLeft)
        boardView.setGameSpeed(gameLoop.getNumOfFramesForCurrentLevel())
        boardView.startAnimatingUp()
        boardView.newRowBlocks = gameLoop.newRow
        boardView.risingAnimationCounter = tempCurrentFrameCount
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(matchAnimationCountKey, gameLoop.blockMatchAnimating)
        outState.putInt(linesToWinKey, gameLoop.numOfLinesLeft)
        outState.putInt(gameSpeedKey, gameLoop.gameSpeedLevel)
        outState.putInt(linesUntilSpeedIncreaseKey, gameLoop.linesToNewLevel)
        outState.putInt(frameCountKey, gameLoop.currentFrameCount)
        outState.putInt(frameCountInWarningKey, gameLoop.framesInWarning)
        outState.putSerializable(newRowKey, gameLoop.newRow)
    }

    override fun boardSwipedUp() {
        if (gameLoop.canAnimateUp()) {
            gameLoop.addNewRow()
        }
    }

    override fun blockFinishedMatchAnimation(row: Int, column: Int) {
        super.blockFinishedMatchAnimation(row, column)
        gameLoop.aBlockFinishedAnimating()
    }

    override fun createGameLoop(): TimeZoneGameLoop {
        return TimeZoneGameLoop(getGameBoard(), tempNumOfLinesLeft, tempGameSpeed)
    }

    override fun gameStatusChanged(newStatus: GameStatus) {
        // Check if game status actually changed
        if (mPrevGameStatus == newStatus || newStatus == GameStatus.Stopped || mPrevGameStatus == GameStatus.Stopped) {
            return
        }

        boardView.statusChanged(newStatus)

        if (mPrevGameStatus == GameStatus.Warning && (newStatus == GameStatus.Running || newStatus == GameStatus.Panic)) {
            gameLoop.moveBlockSwitcherFromTop()
        }

        if (newStatus == GameStatus.Running && mPrevGameStatus != GameStatus.Running) {
            listener?.changeSong(false)
        } else if ((newStatus == GameStatus.Warning || newStatus == GameStatus.Panic) && mPrevGameStatus != GameStatus.Warning && mPrevGameStatus != GameStatus.Panic) {
            listener?.changeSong(true)
        }

        mPrevGameStatus = newStatus
    }

    override fun blocksMatched() {}

    override fun gameFinished(didWin: Boolean) {
        val newFragment = GameDialogFragment.newInstance(didWin)
        newFragment.show(activity?.fragmentManager, "postDialog")
    }

    override fun newBlockWasAdded(numOfLinesLeft: Int) {
        drawLineIfNeeded(numOfLinesLeft)
        boardView.resetRisingAnimationCount()
        boardView.newRowBlocks = gameLoop.newRow
        boardView.setGameSpeed(gameLoop.getNumOfFramesForCurrentLevel())
    }

    override fun updateGameTimeAndSpeed(timeInMilli: Long, gameSpeed: Int, delayInSeconds: Int) {
        listener?.updateGameTimeAndSpeed(timeInMilli, gameSpeed, delayInSeconds)
    }

    override fun startAnimatingUp() {
        boardView.startAnimatingUp()
    }

    override fun stopAnimatingUp() {
        boardView.stopAnimatingUp()
    }

    private fun getGameBoard(): Array<Array<Block>> {
        val grid = Array(NUM_OF_ROWS) { i -> Array(NUM_OF_COLS) { j -> Block(0, j, i)} }
        val columnCounter = IntArray(NUM_OF_COLS)

        var numberNumberOfBLocksLeft = NUM_OF_COLS * NUMBER_OF_BLOCKS_MULTIPLIER

        // Populate first 3 rows
        for (i in NUM_OF_ROWS - 1 downTo NUM_OF_ROWS - 4 + 1) {
            for (j in 0 until NUM_OF_COLS) {
                grid[i][j] = Block(rand.nextInt(7) + 1, j, i)
                columnCounter[j]++
                numberNumberOfBLocksLeft--
            }
        }

        var x = 0
        while (x < numberNumberOfBLocksLeft) {
            val position = rand.nextInt(NUM_OF_COLS)
            if (columnCounter[position] < NUM_OF_ROWS - 1) {
                grid[NUM_OF_ROWS - 1 - columnCounter[position]][position] = Block(rand.nextInt(7) + 1, position, NUM_OF_ROWS - 1 - columnCounter[position])
                columnCounter[position]++
                x++
            }
        }

        return grid

    }

    fun drawLineIfNeeded(numOfLines: Int) {
        if (numOfLines <= NUM_OF_ROWS) {
            boardView.winLineAt(numOfLines)
        }
    }

    interface TimeZoneFragmentInterface {
        fun changeSong(isPanic: Boolean)
        fun updateGameTimeAndSpeed(timeInMilli: Long, gameSpeed: Int, delayInSeconds: Int)
    }

    companion object {
        private const val linesToWinKey = "LINES_TO_WIN_KEY"
        private const val linesUntilSpeedIncreaseKey = "LINES_UNTIL_SPEED_INCREASE_KEY"
        private const val matchAnimationCountKey = "MATCH_ANIMATION_COUNT_KEY"
        private const val gameSpeedKey = "GAME_SPEED_KEY"
        private const val frameCountKey = "FRAME_COUNT_KEY"
        private const val frameCountInWarningKey = "FRAME_COUNT_IN_WARNING_KEY"
        private const val newRowKey = "NEW_BLOCK_ROW_KEY"

        private const val NUMBER_OF_BLOCKS_MULTIPLIER = 5
    }
}