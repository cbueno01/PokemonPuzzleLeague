package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame

import android.graphics.Point

interface IBoard {
    fun switchBlock(switcherLeftBlock: Point)
    fun boardSwipedUp()
    fun switchBlockMoved()
}
