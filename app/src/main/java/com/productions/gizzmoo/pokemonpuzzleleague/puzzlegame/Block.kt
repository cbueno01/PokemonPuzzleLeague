package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame

import android.graphics.Point
import com.productions.gizzmoo.pokemonpuzzleleague.Direction
import java.io.Serializable

class Block(t: Int, x: Int, y: Int) :  Comparable<Block>, Serializable {
    private var coordPoint: Point = Point(0, 0)

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
//        private set
    var removeComboFlagOnNextFrame: Boolean = false
//        private set

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
            6 -> BlockType.TRAINER
            7 -> BlockType.DIAMOND
            else -> BlockType.EMPTY
        }

        coordPoint.x = x
        coordPoint.y = y

        resetBlockValues()
    }

    fun clear() {
        type = BlockType.EMPTY
        resetBlockValues()
    }

    override fun compareTo(other: Block): Int {
        val p1 = coordPoint
        val p2 = other.coordPoint

        return if (p1.y == p2.y) {
            p1.x - p2.x
        } else {
            p1.y - p2.y
        }
    }

    fun changeCoords(newX: Int, newY: Int) {
        coordPoint.x = newX
        coordPoint.y = newY
    }

    fun startFallingAnimation() {
        downAnimatingCount = 0
        isAnimatingDown = true
    }

    fun stopFallingAnimation() {
        downAnimatingCount = 0
        isAnimatingDown = false
    }

    fun startSwitchAnimation(direction: Direction) {
        isBeingSwitched = true
        switchAnimationCount = 1
        leftRightAnimationDirection = direction
    }

    fun stopSwitchAnimation() {
        isBeingSwitched = false
        switchAnimationCount = 0
        leftRightAnimationDirection = Direction.None
    }

    fun blockMatched(delayedMatchAnimationFrames: Int, matchInvertedAnimationFrames: Int, clearMatchFrames: Int, position: Int, totalNumOfBlockMatches: Int) {
        hasMatched = true
        matchPopAnimationCount = 0
        delayMatchAnimationCount = delayedMatchAnimationFrames
        matchInvertedAnimationCount = matchInvertedAnimationFrames
        clearMatchCount = clearMatchFrames
        popPosition = position
        matchTotalCount = totalNumOfBlockMatches
    }

    fun incrementSwitchAnimationFrame() {
        switchAnimationCount++
    }

    fun incrementDownAnimationFrame() {
        downAnimatingCount++
    }

    fun incrementPopAnimationFrame() {
        matchPopAnimationCount++
    }

    fun decrementDelayedMatchAnimationFrame() {
        delayMatchAnimationCount--
    }

    fun decrementInvertedAnimationFrame() {
        matchInvertedAnimationCount--
    }

    fun decrementClearFrame() {
        clearMatchCount--
    }

    fun blockPopped() {
        hasPopped = true
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
    }
}