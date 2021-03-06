package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.risinggame

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.productions.gizzmoo.pokemonpuzzleleague.R
import com.productions.gizzmoo.pokemonpuzzleleague.ViewUtils
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.Block
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameCountDownTimer
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameDialogFragment
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameFragment
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameLoop
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.GameStatus
import java.util.Random

abstract class RisingGameFragment<T : RisingGameLoopListener, U : RisingGameLoop<T>, V : RisingPuzzleBoardView> : GameFragment<T, U, V>(), RisingGameLoopListener {
    var listener: RisingFragmentInterface? = null
    protected var tempGameSpeed: Int = 0
    private var tempBlockMatchAnimating: Int = 0
    private var tempLinesUntilSpeedIncrease: Int = -1 // -1 so game loop knows to get the full amount of rows needed
    private var tempCurrentFrameCount: Int = 0
    private var tempFrameCountInWarning: Int = 0
    private var tempNewBlockRow = RisingGameLoop.createEmptyBlocksRow()
    private var prevGameStatus: GameStatus = GameStatus.Stopped

    private var readyContainerView: RelativeLayout? = null
    private var countDownView: TextView? = null
    private var gameCountDownTimer: GameCountDownTimer? = null
    private var gameCounterExecuted = false

    protected var rand: Random = Random()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = activity as RisingFragmentInterface
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            tempBlockMatchAnimating = savedInstanceState.getInt(matchAnimationCountKey)
            tempGameSpeed = savedInstanceState.getInt(gameSpeedKey)
            tempLinesUntilSpeedIncrease = savedInstanceState.getInt(linesUntilSpeedIncreaseKey)
            tempCurrentFrameCount = savedInstanceState.getInt(frameCountKey)
            tempFrameCountInWarning = savedInstanceState.getInt(frameCountInWarningKey)
            tempNewBlockRow = savedInstanceState.getSerializable(newRowKey) as Array<Block>
            gameCounterExecuted = savedInstanceState.getBoolean(gameCounterExecutedKey)
        } else {
            tempGameSpeed = getGameSpeed()
            tempNewBlockRow = RisingGameLoop.createNewRowBlocks(rand, RisingGameLoop.shouldShowDiamonds(tempGameSpeed))
        }
    }

    override fun onStop() {
        super.onStop()
        tempGameSpeed = gameLoop.gameSpeedLevel
        tempBlockMatchAnimating = gameLoop.blockMatchAnimating
        tempLinesUntilSpeedIncrease = gameLoop.linesToNewLevel
        tempCurrentFrameCount = gameLoop.currentFrameCount
        tempFrameCountInWarning = gameLoop.framesInWarning
        tempNewBlockRow = gameLoop.newRow
        gameCountDownTimer?.cancel()
    }

    override fun onStart() {
        super.onStart()
        prevGameStatus = gameLoop.status
        gameLoop.setMarathonGameProperties(tempNewBlockRow, tempBlockMatchAnimating, tempLinesUntilSpeedIncrease, tempCurrentFrameCount, tempFrameCountInWarning)
        boardView.setGameSpeed(gameLoop.getNumOfFramesForCurrentLevel())
        boardView.newRowBlocks = gameLoop.newRow
        boardView.setRisingAnimationCounter(tempCurrentFrameCount)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(matchAnimationCountKey, gameLoop.blockMatchAnimating)
        outState.putInt(gameSpeedKey, gameLoop.gameSpeedLevel)
        outState.putInt(linesUntilSpeedIncreaseKey, gameLoop.linesToNewLevel)
        outState.putInt(frameCountKey, gameLoop.currentFrameCount)
        outState.putInt(frameCountInWarningKey, gameLoop.framesInWarning)
        outState.putSerializable(newRowKey, gameLoop.newRow)
        outState.putBoolean(gameCounterExecutedKey, gameCounterExecuted)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        readyContainerView = RelativeLayout(activity).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            visibility = View.GONE
            setBackgroundColor(ContextCompat.getColor(context, R.color.game_shadow))
            setOnClickListener {}
        }
        val readyTextView = TextView(context).apply {
            layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                setMargins(0, 0, 0, ViewUtils.pxFromDp(context, 8).toInt())
            }
            text = resources.getString(R.string.ready)
            id = View.generateViewId()
        }
        countDownView = TextView(context).apply {
            layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                addRule(RelativeLayout.BELOW, readyTextView.id)
                addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
            }
        }

        readyContainerView?.addView(readyTextView)
        readyContainerView?.addView(countDownView)
        (view as ViewGroup).addView(readyContainerView)
    }

    override fun boardSwipedUp() {
        if (gameLoop.canAnimateUp()) {
            gameLoop.addNewRow()
        }
    }

    override fun gameStatusChanged(newStatus: GameStatus) {
        // Check if game status actually changed
        if (prevGameStatus == newStatus || newStatus == GameStatus.Stopped || prevGameStatus == GameStatus.Stopped) {
            return
        }

        if (prevGameStatus == GameStatus.Warning && (newStatus == GameStatus.Running || newStatus == GameStatus.Panic || newStatus == GameStatus.InDanger)) {
            gameLoop.moveBlockSwitcherFromTop()
            gameLoop.resetCurrentFrameCount()
        }

        setGameMusicByStatus(newStatus)
        setAnimationPropertiesByStatus()

        prevGameStatus = newStatus
    }

    override fun gameFinished(didWin: Boolean) {
        val newFragment = GameDialogFragment.newInstance(didWin)
        newFragment.show(activity?.supportFragmentManager, "postDialog")
        listener?.onGameFinished()
    }

    override fun newBlockWasAdded() {
        boardView.resetRisingAnimationCount()
        boardView.newRowBlocks = gameLoop.newRow
        boardView.setGameSpeed(gameLoop.getNumOfFramesForCurrentLevel())
    }

    override fun updateGameTimeAndSpeed(timeInMilli: Long, gameSpeed: Int, delayInSeconds: Int) {
        listener?.updateGameTimeAndSpeed(timeInMilli, gameSpeed, delayInSeconds)
    }

    override fun tryToStartAnimatingUp() {
        if (gameLoop.status != GameStatus.Warning && gameLoop.status != GameStatus.Stopped) {
            boardView.startAnimatingUp()
        }
    }

    override fun stopAnimatingUp() {
        boardView.stopAnimatingUp()
    }

    override fun startGame() {
        if (!gameCounterExecuted) {
            readyContainerView?.visibility = View.VISIBLE
            gameCountDownTimer = GameCountDownTimer(onFinishTimer(), onTimerUpdate())
            gameCountDownTimer?.start()
        } else {
            super.startGame()
            listener?.onGameStarted()
        }
    }

    override fun gameIsPrepared() {
        setAnimationPropertiesByStatus()
    }

    private fun onFinishTimer(): () -> Unit {
        return {
            gameCounterExecuted = true
            readyContainerView?.visibility = View.GONE
            super.startGame()
            listener?.onGameStarted()
        }
    }

    private fun onTimerUpdate(): (Int) -> Unit {
        return { value ->
            countDownView?.text = value.toString()
        }
    }

    private fun setGameMusicByStatus(newStatus: GameStatus) {
        if (newStatus == GameStatus.Running && prevGameStatus != GameStatus.Running) {
            listener?.changeSong(false)
        } else if ((newStatus == GameStatus.Warning || newStatus == GameStatus.Panic || newStatus == GameStatus.InDanger) && prevGameStatus != GameStatus.Warning && prevGameStatus != GameStatus.Panic && prevGameStatus != GameStatus.InDanger) {
            listener?.changeSong(true)
        }
    }

    private fun setAnimationPropertiesByStatus() {
        if ((gameLoop.status == GameStatus.Warning || gameLoop.status == GameStatus.Stopped) || !gameLoop.canAnimateUp()) {
            boardView.stopAnimatingUp()
            gameLoop.blockSwitcher.allowedToBeOnTop = true
        } else {
            boardView.startAnimatingUp()
            gameLoop.blockSwitcher.allowedToBeOnTop = false
        }

        boardView.isInDanger = gameLoop.status == GameStatus.InDanger
        boardView.isInWarning = gameLoop.status == GameStatus.Warning
    }

    abstract fun getGameSpeed(): Int

    companion object {
        private const val linesUntilSpeedIncreaseKey = "LINES_UNTIL_SPEED_INCREASE_KEY"
        private const val matchAnimationCountKey = "MATCH_ANIMATION_COUNT_KEY"
        private const val gameSpeedKey = "GAME_SPEED_KEY"
        private const val frameCountKey = "FRAME_COUNT_KEY"
        private const val frameCountInWarningKey = "FRAME_COUNT_IN_WARNING_KEY"
        private const val newRowKey = "NEW_BLOCK_ROW_KEY"
        private const val gameCounterExecutedKey = "COUNTER_EXECUTED_KEY"

        fun getGameBoard(rand: Random, gameSpeedLevel: Int, numOfBlocks: Int, numOfFullRows: Int): Array<Array<Block>> {
            val grid = Array(GameLoop.NUM_OF_ROWS) { i -> Array(GameLoop.NUM_OF_COLS) { j -> Block(0, j, i) } }
            val columnCounter = IntArray(GameLoop.NUM_OF_COLS)

            var numberNumberOfBLocksLeft = numOfBlocks

            // Populate first 3 rows
            for (i in GameLoop.NUM_OF_ROWS - 1 downTo GameLoop.NUM_OF_ROWS - numOfFullRows) {
                for (j in 0 until GameLoop.NUM_OF_COLS) {
                    grid[i][j] = RisingGameLoop.getRandomBlock(rand, j, i, RisingGameLoop.shouldShowDiamonds(gameSpeedLevel))
                    columnCounter[j]++
                    numberNumberOfBLocksLeft--
                }
            }

            var x = 0
            while (x < numberNumberOfBLocksLeft) {
                val position = rand.nextInt(GameLoop.NUM_OF_COLS)
                if (columnCounter[position] < GameLoop.NUM_OF_ROWS - 1) {
                    grid[GameLoop.NUM_OF_ROWS - 1 - columnCounter[position]][position] = RisingGameLoop.getRandomBlock(rand, position, GameLoop.NUM_OF_ROWS - 1 - columnCounter[position], RisingGameLoop.shouldShowDiamonds(gameSpeedLevel))
                    columnCounter[position]++
                    x++
                }
            }

            return grid
        }
    }
}