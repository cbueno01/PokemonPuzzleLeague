package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame

import android.graphics.Point
import java.io.Serializable

class SwitchBlocks(private var leftColumn: Int, private var leftRow: Int, private var rightColumn: Int, private var rightRow: Int) : Serializable {
    var switcherIsBeingMoved: Boolean = false
    var allowedToBeOnTop: Boolean = true

    init {
        if (!isSwitcherStable) {
            throw RuntimeException("Switcher is not stable")
        }
    }

    val isAtTop: Boolean
        get() = leftRow <= 1

    val leftBlock: Point
        get() = Point(leftColumn, leftRow)

    fun setLeftBlock(p: Point) {
        if (p.x < 0 || p.x > 4 || p.y < 0 || p.y > 11) {
            throw RuntimeException("Point is out of bound")
        }

        setCoordinates(p.x, p.y, p.x + 1, p.y)
    }

    fun setRightBlock(p: Point) {
        if (p.x < 1 || p.x > 5 || p.y < 0 || p.y > 11) {
            throw RuntimeException("Point is out of bound")
        }

        setCoordinates(p.x - 1, p.y, p.x, p.y)
    }

    fun areCoordinatesInSwitcher(x: Int, y: Int): Boolean = areCoordinatesInLeftBlock(x, y) || areCoordinatesInRightBlock(x, y)

    fun areCoordinatesInLeftBlock(x: Int, y: Int): Boolean = leftColumn == x && leftRow == y

    fun areCoordinatesInRightBlock(x: Int, y: Int): Boolean = rightColumn == x && rightRow == y

    fun moveUp() {
        if (leftRow > 0 && rightRow > 0) {
            setCoordinates(leftColumn, leftRow - 1, rightColumn, rightRow - 1)
        }
    }

    fun moveDown() {
        if (leftRow < 11 && rightRow < 11) {
            setCoordinates(leftColumn, leftRow + 1, rightColumn, rightRow + 1)
        }
    }

    private val isSwitcherStable: Boolean
        get() = leftRow == rightRow && leftColumn == rightColumn - 1

    private fun setCoordinates(leftC: Int, leftR: Int, rightC: Int, rightR: Int) {
        leftRow = leftR
        leftColumn = leftC
        rightRow = rightR
        rightColumn = rightC

        if (!isSwitcherStable) {
            throw RuntimeException("Switcher is not stable")
        }
    }
}