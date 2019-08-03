package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame

import android.graphics.Point

internal interface IBoard {
    fun switchBlock(switcherLeftBlock: Point)
    fun boardSwipedUp()
    fun blockFinishedMatchAnimation(row: Int, column: Int)
    fun blockIsPopping(position: Int, total: Int)
    fun needsBlockSwap(b1: Block, b2: Block)
    fun switchBlockMoved()
}
