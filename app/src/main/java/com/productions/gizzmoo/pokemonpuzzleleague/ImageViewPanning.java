package com.productions.gizzmoo.pokemonpuzzleleague;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Chrystian on 6/12/2018.
 */

public class ImageViewPanning extends View {
    private final int backgroundImageResID = R.drawable.menu_background;

    Context mContext;
    private Bitmap mImageBitmap;
    private Rect mScreenDimensions = new Rect();
    private Rect mProportionalRect = new Rect();

    private int mFramesNeedFullView;
    private int mCurrentFrame;
    private boolean mGoingRight;

    public ImageViewPanning(Context context) {
        super(context);
        init(context);
    }

    public ImageViewPanning(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ImageViewPanning(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mImageBitmap = BitmapFactory.decodeResource(mContext.getResources(), backgroundImageResID);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mScreenDimensions.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float scalingFactor = (float)mScreenDimensions.bottom / mImageBitmap.getHeight();
        float scaledWidth = scalingFactor * mImageBitmap.getWidth();
        float percentWidth =  mScreenDimensions.right / scaledWidth;
        int widthToShow = (int)(mImageBitmap.getWidth() * percentWidth);

        float frameMoveSize = (float)(mImageBitmap.getWidth() - widthToShow) / mFramesNeedFullView;

        mProportionalRect.set((int)(frameMoveSize * mCurrentFrame), 0, (int)(frameMoveSize * mCurrentFrame) + widthToShow, mImageBitmap.getHeight());
        canvas.drawBitmap(mImageBitmap, mProportionalRect, mScreenDimensions, null);
    }

    public void framesToLookAtView(int frames) {
        mFramesNeedFullView = frames;
    }

    public void setCurrentFrame(int currentFrame) {
        if (currentFrame < 0) {
            currentFrame = 0;
        } else if (currentFrame > mFramesNeedFullView) {
            currentFrame = mFramesNeedFullView;
        }

        mCurrentFrame = currentFrame;
    }

    public void setDirectionRight(boolean isGoingRight) {
        mGoingRight = isGoingRight;
    }

    public void moveFrame() {
        if (mGoingRight && mCurrentFrame >= mFramesNeedFullView) {
            mGoingRight = false;
            mCurrentFrame--;
        } else if (!mGoingRight && mCurrentFrame < 1) {
            mGoingRight = true;
            mCurrentFrame++;
        } else if (mGoingRight) {
            mCurrentFrame++;
        } else {
            mCurrentFrame--;
        }
    }

    public int getCurrentImageFrame() {
        return mCurrentFrame;
    }

    public boolean isPanningRight() {
        return mGoingRight;
    }
}
