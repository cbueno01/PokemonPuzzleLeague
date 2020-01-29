package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame

import com.productions.gizzmoo.pokemonpuzzleleague.Direction
import java.io.Serializable

class Block(t: Int, x: Int, y: Int) :  Comparable<Block>, Serializable {
    private var xCoord = 0
    private var yCoord = 0

    var type: BlockType
        private set

    var isBeingSwitched: Boolean = false
        private set
    var switchAnimationCount: Int = 0
        private set
    var leftRightAnimationDirection: Direction = Direction.None
        private set

    var isAnimatingDown: Boolean = false
        private set
    var downAnimatingCount: Int = 0
        private set
    var hasMatched: Boolean = false
        private set
    var delayMatchAnimationCount: Int = 0
        private set
    var matchInvertedAnimationCount: Int = 0
        private set
    var matchPopAnimationCount: Int = 0
        private set
    var clearMatchCount: Int = 0
        private set
    var matchTotalCount: Int = 0
        private set
    var hasPopped: Boolean = false
        private set
    var popPosition: Int = 0
        private set

    var canCombo: Boolean = false
        private set
    var removeComboFlagOnNextFrame: Boolean = false
        private set
    var comboCount = 0
        private set

    var maxComboInMatch = 0
        private set

    val canInteract: Boolean
        get() = !isBlockEmpty && !isAnimating

    val isAnimating: Boolean
        get() = isAnimatingDown || hasMatched || isBeingSwitched

    val isBlockEmpty: Boolean
        get() = type === BlockType.EMPTY

    val isAnimatingLeft: Boolean
        get() = leftRightAnimationDirection === Direction.Left

    init {
        type = when (t) {
            1 -> BlockType.LEAF
            2 -> BlockType.FIRE
            3 -> BlockType.HEART
            4 -> BlockType.WATER
            5 -> BlockType.COIN
            6 -> BlockType.DIAMOND
            7 -> BlockType.TRAINER
            else -> BlockType.EMPTY
        }

        xCoord = x
        yCoord = y

        resetBlockValues()
    }

    @Synchronized
    fun clear() {
        type = BlockType.EMPTY
        resetBlockValues()
    }

    override fun compareTo(other: Block): Int {
        return if (this.yCoord == other.yCoord) {
            this.xCoord - other.xCoord
        } else {
            this.yCoord - other.yCoord
        }
    }

    @Synchronized
    fun changeCoords(newX: Int, newY: Int) {
        this.xCoord = newX
        this.yCoord = newY
    }

    @Synchronized
    fun startFallingAnimation() {
        downAnimatingCount = 0
        isAnimatingDown = true
    }

    @Synchronized
    fun stopFallingAnimation() {
        downAnimatingCount = 0
        isAnimatingDown = false
    }

    @Synchronized
    fun startSwitchAnimation(direction: Direction) {
        isBeingSwitched = true
        switchAnimationCount = 1
        leftRightAnimationDirection = direction
    }

    @Synchronized
    fun stopSwitchAnimation() {
        isBeingSwitched = false
        switchAnimationCount = 0
        leftRightAnimationDirection = Direction.None
    }

    @Synchronized
    fun blockMatched(delayedMatchAnimationFrames: Int, matchInvertedAnimationFrames: Int, clearMatchFrames: Int, position: Int, totalNumOfBlockMatches: Int) {
        hasMatched = true
        matchPopAnimationCount = 0
        delayMatchAnimationCount = delayedMatchAnimationFrames
        matchInvertedAnimationCount = matchInvertedAnimationFrames
        clearMatchCount = clearMatchFrames
        popPosition = position
        matchTotalCount = totalNumOfBlockMatches
    }

    @Synchronized
    fun incrementSwitchAnimationFrame() {
        switchAnimationCount++
    }

    @Synchronized
    fun incrementDownAnimationFrame() {
        downAnimatingCount++
    }

    @Synchronized
    fun incrementPopAnimationFrame() {
        matchPopAnimationCount++
    }

    @Synchronized
    fun decrementDelayedMatchAnimationFrame() {
        delayMatchAnimationCount--
    }

    @Synchronized
    fun decrementInvertedAnimationFrame() {
        matchInvertedAnimationCount--
    }

    @Synchronized
    fun decrementClearFrame() {
        clearMatchCount--
    }

    @Synchronized
    fun blockPopped() {
        hasPopped = true
    }

    @Synchronized
    fun setCanComboFlag(flag: Boolean) {
        canCombo = flag
    }

    @Synchronized
    fun setRemoveComboFlagOnNextFrame(flag: Boolean) {
        removeComboFlagOnNextFrame = flag
    }

    @Synchronized
    fun resetComboCount() {
        comboCount = 0
    }

    @Synchronized
    fun setComboCount(count: Int) {
        comboCount = count
    }

    @Synchronized
    fun setMaxComboForMatch(count: Int) {
        maxComboInMatch = count
    }

    private fun resetBlockValues() {
        isBeingSwitched = false
        switchAnimationCount = 0
        leftRightAnimationDirection = Direction.None

        isAnimatingDown = false
        downAnimatingCount = 0
        hasMatched = false
        delayMatchAnimationCount = 0
        matchInvertedAnimationCount = 0
        matchPopAnimationCount = 0
        clearMatchCount = 0
        matchTotalCount = 0
        hasPopped = false
        popPosition = 0

        canCombo = false
        removeComboFlagOnNextFrame = false
        comboCount = 0
        maxComboInMatch = 0
    }
}