package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.productions.gizzmoo.pokemonpuzzleleague.R

object BoardResources {
    /**********************************
     * All BlockTypes must be populated
     * All block resources must be the same size!
     **********************************/

    private val defaultType = BlockType.LEAF

    private val normalResources = hashMapOf(
            BlockType.LEAF to R.drawable.leaf_normal,
            BlockType.FIRE to R.drawable.fire_normal,
            BlockType.HEART to R.drawable.heart_normal,
            BlockType.WATER to R.drawable.water_normal,
            BlockType.COIN to R.drawable.coin_normal,
            BlockType.TRAINER to R.drawable.metallic_normal,
            BlockType.DIAMOND to R.drawable.diamond_normal)

    private val darkResources = hashMapOf(
            BlockType.LEAF to R.drawable.leaf_dark,
            BlockType.FIRE to R.drawable.fire_dark,
            BlockType.HEART to R.drawable.heart_dark,
            BlockType.WATER to R.drawable.water_dark,
            BlockType.COIN to R.drawable.coin_dark,
            BlockType.TRAINER to R.drawable.metallic_dark,
            BlockType.DIAMOND to R.drawable.diamond_dark)

    private val invertedResources = hashMapOf(
            BlockType.LEAF to R.drawable.leaf_inverted,
            BlockType.FIRE to R.drawable.fire_inverted,
            BlockType.HEART to R.drawable.heart_inverted,
            BlockType.WATER to R.drawable.water_inverted,
            BlockType.COIN to R.drawable.coin_inverted,
            BlockType.TRAINER to R.drawable.metallic_inverted,
            BlockType.DIAMOND to R.drawable.diamond_inverted)

    private val popAnimation1Resources = hashMapOf(
            BlockType.LEAF to R.drawable.leaf_pop_animation_1,
            BlockType.FIRE to R.drawable.fire_pop_animation_1,
            BlockType.HEART to R.drawable.heart_pop_animation_1,
            BlockType.WATER to R.drawable.water_pop_animation_1,
            BlockType.COIN to R.drawable.coin_pop_animation_1,
            BlockType.TRAINER to R.drawable.metallic_pop_animation_1,
            BlockType.DIAMOND to R.drawable.diamond_pop_animation_1)

    private val popAnimation2Resources = hashMapOf(
            BlockType.LEAF to R.drawable.leaf_pop_animation_2,
            BlockType.FIRE to R.drawable.fire_pop_animation_2,
            BlockType.HEART to R.drawable.heart_pop_animation_2,
            BlockType.WATER to R.drawable.water_pop_animation_2,
            BlockType.COIN to R.drawable.coin_pop_animation_2,
            BlockType.TRAINER to R.drawable.metallic_pop_animation_2,
            BlockType.DIAMOND to R.drawable.diamond_pop_animation_2)

    private val popAnimation4Resources = hashMapOf(
            BlockType.LEAF to R.drawable.leaf_pop_animation_4,
            BlockType.FIRE to R.drawable.fire_pop_animation_4,
            BlockType.HEART to R.drawable.heart_pop_animation_4,
            BlockType.WATER to R.drawable.water_pop_animation_4,
            BlockType.COIN to R.drawable.coin_pop_animation_4,
            BlockType.TRAINER to R.drawable.metallic_pop_animation_4,
            BlockType.DIAMOND to R.drawable.diamond_pop_animation_4)

    private val popAnimation5Resources = hashMapOf(
            BlockType.LEAF to R.drawable.leaf_pop_animation_5,
            BlockType.FIRE to R.drawable.fire_pop_animation_5,
            BlockType.HEART to R.drawable.heart_pop_animation_5,
            BlockType.WATER to R.drawable.water_pop_animation_5,
            BlockType.COIN to R.drawable.coin_pop_animation_5,
            BlockType.TRAINER to R.drawable.metallic_pop_animation_5,
            BlockType.DIAMOND to R.drawable.diamond_pop_animation_5)

    private val normalBitmaps: HashMap<BlockType, Bitmap> = HashMap()
    private val darkBitmaps: HashMap<BlockType, Bitmap> = HashMap()
    private val invertedBitmaps: HashMap<BlockType, Bitmap> = HashMap()
    private val popAnimationBitmaps: HashMap<BlockType, Array<Bitmap>> = HashMap()

    private var blankBitmap: Bitmap? = null

    fun createImageBitmaps(context: Context) {
        for (type in BlockType.values().filter { type -> type !== BlockType.EMPTY }) {
            normalBitmaps[type] = BitmapFactory.decodeResource(context.resources, normalResources[type]!!)
            darkBitmaps[type] = BitmapFactory.decodeResource(context.resources, darkResources[type]!!)
            invertedBitmaps[type] = BitmapFactory.decodeResource(context.resources, invertedResources[type]!!)

            popAnimationBitmaps[type] = arrayOf(
                BitmapFactory.decodeResource(context.resources, popAnimation1Resources[type]!!),
                BitmapFactory.decodeResource(context.resources, popAnimation2Resources[type]!!),
                BitmapFactory.decodeResource(context.resources, popAnimation4Resources[type]!!),
                BitmapFactory.decodeResource(context.resources, popAnimation5Resources[type]!!)
            )
        }

        blankBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.blank_block)
    }

    fun getNormalBlock(type: BlockType): Bitmap? = normalBitmaps[type]

    fun getDarkBlock(type: BlockType): Bitmap? = darkBitmaps[type]

    fun getInvertedBlock(type: BlockType): Bitmap? = invertedBitmaps[type]

    fun getPopAnimationBlock(type: BlockType, animationSection: Int): Bitmap? {
        val arr = popAnimationBitmaps[type]!!
        return when (animationSection) {
            0, 2 -> arr[0]
            1 -> arr[1]
            3 -> arr[2]
            4 -> arr[3]
            else -> blankBitmap
        }
    }

    fun getBlockHeights(): Int = normalBitmaps[defaultType]?.width ?: 0
}