package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame

import android.os.AsyncTask
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.PuzzleBoardView.Companion.ANIMATION_FALLING_FRAMES_NEEDED
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.PuzzleBoardView.Companion.ANIMATION_MATCH_INVERT_FRAMES_NEEDED
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.PuzzleBoardView.Companion.ANIMATION_MATCH_POP_FRAMES_NEEDED
import com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame.PuzzleBoardView.Companion.ANIMATION_SWITCH_FRAMES_NEEDED
import java.util.concurrent.locks.ReentrantLock

abstract class GameLoop<T : GameLoopListener> : AsyncTask<Void, Void, Void>() {
    var elapsedTime: Long = 0
        private set
    private var startTime = 0L
    private var previousElapsedTime = 0L

    protected val blockMatch: ArrayList<Block> = ArrayList()
    protected val gridLock = ReentrantLock(true)
    lateinit var grid: Array<Array<Block>>
        private set
    lateinit var blockSwitcher: SwitchBlocks
        private set
    var listener: T? = null

    protected var didWin: Boolean = false
    var status: GameStatus = GameStatus.Running
        private set

    override fun doInBackground(vararg voids: Void): Void? {
        while (status != GameStatus.Stopped && !isCancelled) {
            elapsedTime = System.nanoTime() / 1000000 - (startTime - previousElapsedTime)

            try {
                Thread.sleep(FRAME_PERIOD.toLong())
            } catch (e: InterruptedException) {}

            updateBoardAnimations()
            applyGravity()
            checkForMatchesAndCombos()
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
        startTime = System.nanoTime() / 1000000
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
        gridLock.lock()
        for (y in NUM_OF_ROWS - 2 downTo 0) {
            for (x in 0 until NUM_OF_COLS) {
                if (grid[y][x].canInteract) {
                    if ((grid[y + 1][x].isBlockEmpty || grid[y + 1][x].isAnimatingDown) && !(grid[y + 1][x].isBeingSwitched || grid[y + 1][x].hasMatched)) {
                        grid[y][x].startFallingAnimation()
                        swapBlocksInternal(x, y, x, y + 1)
                    }
                }
            }
        }
        gridLock.unlock()
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
            addMaxComboToBlocks()
            playSoundIfNecessary()
            notifyBlocksMatched()
            blockMatch.clear()
        }
    }

    private fun updateBoardAnimations() {
        for (row in grid.indices.reversed()) {
            for (blockIndex in 0 until grid[row].size) {
                val block = grid[row][blockIndex]
                if (!block.isBlockEmpty) {
                    when {
                        block.hasMatched -> updateMatchedBlock(block, row, blockIndex)
                        block.isBeingSwitched -> updateSwitchedBlock(block)
                        block.isAnimatingDown -> updateBlockFalling(block, row, blockIndex)
                        else -> updateComboIfNeeded(block)
                    }
                } else {
                    // Handle blanks that are being switched
                    handleEmptySwitchBlock(block)
                }
            }
        }
    }

    abstract fun getUpdatedGameStatus()

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

    private fun addMaxComboToBlocks() {
        val maxComboCount = getComboCountMax(blockMatch)
        for (block in blockMatch) {
            block.setMaxComboForMatch(maxComboCount)
        }
    }

    private fun playSoundIfNecessary() {
        val comboCount = getComboCountMax(blockMatch)
        val playPokemonSound = comboCount > 0
        if (playPokemonSound) {
            listener?.playPokemonSound(comboCount)
        } else if (blockMatch.size > 3) {
            listener?.playTrainerSound(false)
        }
    }

    protected fun getComboCountMax(blockList: ArrayList<Block>): Int {
        var maxComboCount = 0
        for (block in blockList) {
            if (block.canCombo) {
                maxComboCount = maxOf(block.comboCount, maxComboCount)
            }
        }

        return maxComboCount
    }

    private fun startMatchAnimation() {
        val matchSize = blockMatch.size
        for (i in 0 until matchSize) {
            val b = blockMatch[i]
            val delayedMatchAnimation = i * ANIMATION_MATCH_POP_FRAMES_NEEDED
            b.blockMatched(delayedMatchAnimation, ANIMATION_MATCH_INVERT_FRAMES_NEEDED, (blockMatch.size - 1) * ANIMATION_MATCH_POP_FRAMES_NEEDED - delayedMatchAnimation + ANIMATION_MATCH_INVERT_FRAMES_NEEDED, i, matchSize)
        }
    }

    private fun updateMatchedBlock(block: Block, row: Int, blockIndex: Int) {
        if (block.matchInvertedAnimationCount > 0) {
            block.decrementInvertedAnimationFrame()
        } else if (block.delayMatchAnimationCount > 0) {
            block.decrementDelayedMatchAnimationFrame()
        } else if (block.matchPopAnimationCount < ANIMATION_MATCH_POP_FRAMES_NEEDED) {
            block.incrementPopAnimationFrame()
        } else if (block.clearMatchCount > 0) {
            block.decrementClearFrame()
            if (!block.hasPopped) {
                listener?.blockIsPopping(block.popPosition, block.matchTotalCount)
                block.blockPopped()
            }
        } else {
            blockFinishedMatchAnimation(row, blockIndex)
            block.clear()
        }
    }

    private fun updateBlockFalling(currentBlock: Block, row: Int, blockIndex: Int) {
        var blockNeedsToSwap = false
        if (currentBlock.downAnimatingCount >= ANIMATION_FALLING_FRAMES_NEEDED) {
            val belowBlock = if (row < grid.size - 1) grid[row + 1][blockIndex] else null
            if (belowBlock != null && (belowBlock.isBlockEmpty || belowBlock.isAnimatingDown) && !(belowBlock.isBeingSwitched || belowBlock.hasMatched)) {
                blockNeedsToSwap = true
            } else {
                if (currentBlock.canCombo) {
                    currentBlock.setRemoveComboFlagOnNextFrame(true)
                }
                currentBlock.stopFallingAnimation()
            }
        } else {
            currentBlock.incrementDownAnimationFrame()
        }

        if (blockNeedsToSwap) {
            currentBlock.startFallingAnimation()
            swapBlocks(blockIndex, row, blockIndex, row + 1)
        }
    }

    private fun updateComboIfNeeded(block: Block) {
        if (block.removeComboFlagOnNextFrame) {
            block.setRemoveComboFlagOnNextFrame(false)
            block.setCanComboFlag(false)
            block.resetComboCount()
            block.setMaxComboForMatch(0)
        }
    }

    private fun updateSwitchedBlock(block: Block) {
        block.incrementSwitchAnimationFrame()

        if (block.switchAnimationCount >= ANIMATION_SWITCH_FRAMES_NEEDED) {
            block.stopSwitchAnimation()
        }
    }

    private fun handleEmptySwitchBlock(block: Block) {
            if (block.isBeingSwitched) {
                block.incrementSwitchAnimationFrame()

                if (block.switchAnimationCount >= ANIMATION_SWITCH_FRAMES_NEEDED) {
                    block.clear()
                }
            }
    }

    abstract fun notifyBlocksMatched()

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
        gridLock.lock()
        swapBlocksInternal(x1, y1, x2, y2)
        gridLock.unlock()
    }

    @Synchronized
    protected fun swapBlocksInternal(x1: Int, y1: Int, x2: Int, y2: Int) {
        val blockHolder = grid[y1][x1]
        grid[y1][x1] = grid[y2][x2]
        grid[y2][x2] = blockHolder

        grid[y1][x1].changeCoords(x1, y1)
        grid[y2][x2].changeCoords(x2, y2)
    }

    protected open fun blockFinishedMatchAnimation(row: Int, column: Int) {
        var rowToUpdate = row - 1
        while (rowToUpdate >= 0 && grid[rowToUpdate][column].canInteract) {
            grid[rowToUpdate][column].setCanComboFlag(true)
            if (grid[rowToUpdate][column].comboCount <= grid[row][column].maxComboInMatch) {
                grid[rowToUpdate][column].setComboCount(grid[row][column].maxComboInMatch + 1)
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

    fun setGameProperties(gameGrid: Array<Array<Block>>, switcher: SwitchBlocks, previousElapsedTimeInSec: Long) {
        grid = gameGrid
        blockSwitcher = switcher
        previousElapsedTime = previousElapsedTimeInSec
    }

    companion object {
        const val NUM_OF_COLS = 6
        const val NUM_OF_ROWS = 12
        const val MAX_FPS = 30
        private const val FRAME_PERIOD = 1000 / MAX_FPS
    }
}