package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame

import android.os.AsyncTask
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.PuzzleBoardView.Companion.ANIMATION_MATCH_INVERT_FRAMES_NEEDED
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.PuzzleBoardView.Companion.ANIMATION_MATCH_POP_FRAMES_NEEDED
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

abstract class GameLoop<T : GameLoopListener>(gameGrid: Array<Array<Block>>) : AsyncTask<Void, Void, Void>() {
    protected val lock: Lock = ReentrantLock()
    protected var didWin: Boolean = false
    protected var comboCount: Int = 0
    protected var elapsedTime: Long = 0
    protected val blockMatch: ArrayList<Block> = ArrayList()
    var listener: T? = null
    var grid: Array<Array<Block>> = gameGrid
    var blockSwitcher: SwitchBlocks = SwitchBlocks(2, 9, 3, 9)
        protected set
    var startTime: Long = System.nanoTime() / 1000000
        private set
    var status: GameStatus = GameStatus.Running
        private set

    override fun doInBackground(vararg voids: Void): Void? {
        while (status != GameStatus.Stopped && !isCancelled) {
            elapsedTime = System.nanoTime() / 1000000 - startTime

            try {
                Thread.sleep(FRAME_PERIOD.toLong())
            } catch (e: InterruptedException) {}

            applyGravity()
            checkForMatchesAndCombos()
            checkToResetCombo()
            postGameMechanicHook()
            getUpdatedGameStatus()
            checkIfGameEnded()
            publishProgress()
        }

        return null
    }

    override fun onPreExecute() {
        getUpdatedGameStatus()
        blockMatch.clear()
    }

    override fun onProgressUpdate(vararg values: Void?) {
        listener?.updateBoardView()
    }

    override fun onPostExecute(result: Void?) {
        listener?.gameFinished(didWin)
    }

    fun startGame() {
        this.execute()
    }

    // Gets called on every frame
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private fun applyGravity() {
        for (y in NUM_OF_ROWS - 2 downTo 0) {
            for (x in 0 until NUM_OF_COLS) {
                if (grid[y][x].canInteract) {
                    if ((grid[y + 1][x].isBlockEmpty || grid[y + 1][x].isAnimatingDown) && !(grid[y + 1][x].isBeingSwitched || grid[y + 1][x].hasMatched)) {
                        grid[y][x].startFallingAnimation()
                        swapBlocks(x, y, x, y + 1)
                    }
                }
            }
        }
    }

    private fun checkToResetCombo() {
        for (row in grid) {
            for (block in row) {
                if (block.canCombo || block.removeComboFlagOnNextFrame) {
                    return
                }
            }
        }

        comboCount = 0
    }

    private fun checkForMatchesAndCombos() {
        for (i in 0 until NUM_OF_ROWS) {
            for (j in 0 until NUM_OF_COLS) {
                if (grid[i][j].canInteract) {
                    blockMatch.addAll(checkForMatchWithDirection(i, j, 0))
                    blockMatch.addAll(checkForMatchWithDirection(i, j, 1))
                }
            }
        }

        if (blockMatch.isNotEmpty()) {
            blockMatch.sort()
            removeDuplicateBlocks()
            startMatchAnimation()
            addToComboIfApplicable()
            playSoundIfNecessary()
            notifyBlocksMatched()
            blockMatch.clear()
        }
    }

    private fun getUpdatedGameStatus() {
        if (didWin) {
            changeGameStatus(GameStatus.Stopped)
        } else if (doesRowContainBlock(0)) {
            if (status != GameStatus.Warning) {
                changeGameStatus(GameStatus.Warning)
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
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 0 - Up/Down
    // 1 - Left/Right

    private fun checkForMatchWithDirection(i: Int, j: Int, direction: Int): ArrayList<Block> {
        val tempList = ArrayList<Block>()
        tempList.add(grid[i][j])
        when (direction) {
            0 -> {
                var pos = i - 1
                // Check down
                while (pos >= 0 && grid[i][j].type == grid[pos][j].type && grid[pos][j].canInteract) {
                    tempList.add(grid[pos][j])
                    pos--
                }

                pos = i + 1
                // Check up
                while (pos < NUM_OF_ROWS && grid[i][j].type == grid[pos][j].type && grid[pos][j].canInteract) {
                    tempList.add(grid[pos][j])
                    pos++
                }
            }
            1 -> {
                var pos = j - 1
                // Check right
                while (pos >= 0 && grid[i][j].type == grid[i][pos].type && grid[i][pos].canInteract) {
                    tempList.add(grid[i][pos])
                    pos--
                }

                pos = j + 1
                // Check left
                while (pos < NUM_OF_COLS && grid[i][j].type == grid[i][pos].type && grid[i][pos].canInteract) {
                    tempList.add(grid[i][pos])
                    pos++
                }
            }
        }

        if (tempList.size < 3) {
            tempList.clear()
        }

        return tempList
    }

    private fun addToComboIfApplicable() {
        for (b in blockMatch) {
            if (b.canCombo) {
                comboCount++
                return
            }
        }
    }

    private fun playSoundIfNecessary() {
        val playPokemonSound = comboCount > 0
        if (playPokemonSound) {
            listener?.playPokemonSound(comboCount)
        } else if (blockMatch.size > 3) {
            listener?.playTrainerSound(false)
        }
    }

    private fun startMatchAnimation() {
        val matchSize = blockMatch.size
        for (i in 0 until matchSize) {
            val b = blockMatch[i]
            val delayedMatchAnimation = i * ANIMATION_MATCH_POP_FRAMES_NEEDED
            b.blockMatched(delayedMatchAnimation, ANIMATION_MATCH_INVERT_FRAMES_NEEDED, (blockMatch.size - 1) * ANIMATION_MATCH_POP_FRAMES_NEEDED - delayedMatchAnimation + ANIMATION_MATCH_INVERT_FRAMES_NEEDED, i, matchSize)
        }
    }

    protected open fun notifyBlocksMatched() {
        listener?.blocksMatched()
    }

    private fun removeDuplicateBlocks() {
        val list = ArrayList<Block>()

        var prevBlock: Block? = null

        for (b in blockMatch) {
            if (prevBlock !== b) {
                list.add(b)
            }

            prevBlock = b
        }

        blockMatch.clear()
        blockMatch.addAll(list)
    }

    protected fun changeGameStatus(newStatus: GameStatus) {
        status = newStatus
        listener?.gameStatusChanged(newStatus)
    }

    protected abstract fun checkIfGameEnded()

    protected abstract fun postGameMechanicHook()

    protected fun doesRowContainBlock(row: Int): Boolean {
        for (block in grid[row]) {
            if (!block.isBlockEmpty) {
                return true
            }
        }

        return false
    }

    fun swapBlocks(x1: Int, y1: Int, x2: Int, y2: Int) {
        lock.lock()
        val blockHolder = grid[y1][x1]
        grid[y1][x1] = grid[y2][x2]
        grid[y2][x2] = blockHolder

        grid[y1][x1].changeCoords(x1, y1)
        grid[y2][x2].changeCoords(x2, y2)
        lock.unlock()
    }

    fun blockFinishedMatchAnimation(row: Int, column: Int) {
        var rowToUpdate = row - 1
        while (rowToUpdate >= 0 && !grid[rowToUpdate][column].isBlockEmpty) {
            if (grid[rowToUpdate][column].canInteract) {
                grid[rowToUpdate][column].canCombo = true
            }
            rowToUpdate--
        }
    }

    fun moveBlockSwitcherFromTop() {
        if (blockSwitcher.isAtTop) {
            blockSwitcher.moveDown()
        }
    }

    protected fun isBoardAnimating(): Boolean {
        for (row in grid) {
            for (block in row) {
                if (block.isAnimating) {
                    return true
                }
            }
        }

        return false
    }

    fun setGameProperties(gameGrid: Array<Array<Block>>, switcher: SwitchBlocks, gameStartTime: Long) {
        grid = gameGrid
        blockSwitcher = switcher
        startTime = gameStartTime
    }

    companion object {
        const val NUM_OF_COLS = 6
        const val NUM_OF_ROWS = 12
        const val MAX_FPS = 30
        private const val FRAME_PERIOD = 1000 / MAX_FPS
    }
}