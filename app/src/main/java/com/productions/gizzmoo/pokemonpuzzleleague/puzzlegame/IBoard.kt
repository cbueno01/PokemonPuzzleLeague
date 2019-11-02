package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame

import android.graphics.Point

interface IBoard {
    fun switchBlock(switcherLeftBlock: Point)
    fun boardSwipedUp()
    fun blockFinishedMatchAnimation(row: Int, column: Int)
    fun blockIsPopping(position: Int, total: Int)
    fun needsBlockSwap(block1X: Int, block1Y: Int, block2X: Int, block2Y: Int)
    fun switchBlockMoved()
}
