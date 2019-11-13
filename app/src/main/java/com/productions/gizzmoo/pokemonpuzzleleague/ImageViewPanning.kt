package com.productions.gizzmoo.pokemonpuzzleleague

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class ImageViewPanning(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val backgroundImageResID = R.drawable.menu_background

    private var imageBitmap: Bitmap = BitmapFactory.decodeResource(context.resources, backgroundImageResID)
    private val screenDimensions = Rect()
    private val proportionalRect = Rect()

    var framesNeededForView: Int = 0
    var currentFrame: Int = 0
        private set
    var direction: Direction = Direction.None

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        screenDimensions.set(0, 0, measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val scalingFactor = screenDimensions.bottom.toFloat() / imageBitmap.height
        val scaledWidth = scalingFactor * imageBitmap.width
        val percentWidth = screenDimensions.right / scaledWidth
        val widthToShow = (imageBitmap.width * percentWidth).toInt()

        val frameMoveSize = (imageBitmap.width - widthToShow).toFloat() / framesNeededForView

        proportionalRect.set((frameMoveSize * currentFrame).toInt(), 0, (frameMoveSize * currentFrame).toInt() + widthToShow, imageBitmap.height)
        canvas.drawBitmap(imageBitmap, proportionalRect, screenDimensions, null)
    }

    fun setCurrentFrame(frame: Int) {
        currentFrame = when {
            frame < 0 -> 0
            frame > framesNeededForView -> framesNeededForView
            else -> frame
        }
    }


    fun moveFrame() {
        if (direction == Direction.Right && currentFrame >= framesNeededForView) {
            direction = Direction.Left
            currentFrame--
        } else if (direction == Direction.Left && currentFrame < 1) {
            direction = Direction.Right
            currentFrame++
        } else if (direction == Direction.Right) {
            currentFrame++
        } else {
            currentFrame--
        }
    }
}