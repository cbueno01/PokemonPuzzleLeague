package com.productions.gizzmoo.pokemonpuzzleleague;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import static android.view.MotionEvent.INVALID_POINTER_ID;


/**
 * Created by Chrystian on 1/26/2018.
 */

public class PuzzleBoardView extends View {

    public static final int BOARD_PADDING = 16;
    public static final int ANIMATION_SWITCH_FRAMES_NEEDED = 6;
    public static final int ANIMATION_FALLING_FRAMES_NEEDED = 4;
    public static final int ANIMATION_MATCH_POP_FRAMES_NEEDED = 10;
    public static final int ANIMATION_MATCH_INVERT_FRAMES_NEEDED = 6;
    public static final int MIN_SWIPE_DISTANCE = 3;

    private Block[][] mBlocks;
    private Block[] mNewRowBlocks = new Block[6];
    private SwitchBlocks mBlockSwitcher;
    private Paint mBoardPaint;
    private Paint mBoardBoarderPaint;
    private Paint mBlockSwitcherPaint;

    private int mBoardWidth;
    private int mBoardHeight;
    private int mWidthOffset;
    private int mHeightOffset;
    private int mBlockSize;
    private Rect mBlockRect = new Rect();
    private Rect mBlockRectScale = new Rect();
    private Rect mBoardRect = new Rect();
    private Bitmap mTrainerBitmap;
    private int mNumOfTotalFrames;

    private int mActivePointerId = INVALID_POINTER_ID;
    private Point mLastTouch;
    private Point mLastTouchPointer;
    private boolean mLeftBlockSwitcherIsBeingMoved;
    private boolean mRightBlockSwitcherIsBeingMoved;
    private BoardListener mListener;

    private int mRisingAnimationCounter;
    private int mRisingAnimationOffset;

    private boolean mShouldAnimatingUp;

    private GameStatus mCurrentStatus;

    private int mWinLine;
    private boolean mShouldShowWinLine;

    private Context mContext;

    public PuzzleBoardView(Context context) {
        this(context, null, 0);
    }

    public PuzzleBoardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PuzzleBoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;

        mBoardPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBoardPaint.setColor(Color.BLACK);
        mBoardPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBoardPaint.setAlpha(180); // 70%

        mBoardBoarderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBoardBoarderPaint.setStyle(Paint.Style.STROKE);

        mBlockSwitcherPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBlockSwitcherPaint.setColor(Color.WHITE);
        mBlockSwitcherPaint.setStyle(Paint.Style.STROKE);
        mBlockSwitcherPaint.setStrokeWidth(7);

        for (int i = 0; i < mNewRowBlocks.length; i++) {
            mNewRowBlocks[i] = new Block(0, i, 11);
        }

        mRisingAnimationCounter = 1;
        mNumOfTotalFrames = 1;
        mRisingAnimationOffset = 0;
        mShouldAnimatingUp = false;

        mWinLine = 0;
        mShouldShowWinLine = false;

        BoardResources.createImageBitmaps(mContext);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        int currentTrainer = settings.getInt("pref_trainer_key", 0);
        int trainerResource = TrainerResources.getTrainerFullBody(currentTrainer);

        if (trainerResource != -1) {
            mTrainerBitmap =  BitmapFactory.decodeResource(mContext.getResources(), trainerResource);
        }
    }

    public void setGrid(Block[][] grid) {
        mBlocks = grid;
        invalidate();
        requestLayout();
    }

    public void setGrid(Block[][] grid, SwitchBlocks switcher) {
        mBlockSwitcher = switcher;
        setGrid(grid);
    }

    public void setGameSpeed(int numOfFrames) {
        mNumOfTotalFrames = numOfFrames;
    }

    public void setNewRow(Block[] newRow) {
        mNewRowBlocks = newRow;
    }

    public void setRisingAnimationCount (int count) {
        mRisingAnimationCounter = count;
    }

    public void newRowAdded() {
        mRisingAnimationCounter = 1;
    }

    public void startAnimatingUp() {
        mShouldAnimatingUp = true;
    }

    public void stopAnimatingUp() {
        mShouldAnimatingUp = false;
    }

    public void statusChanged(GameStatus status) {
        mCurrentStatus = status;
    }

    private Point getGridCoordinatesOffXY(float x, float y) {
        int posX = (int)Math.abs((x - BOARD_PADDING - mWidthOffset) / mBlockSize);
        int posY = (int)Math.abs((y - BOARD_PADDING - mHeightOffset) / mBlockSize);

        // Don't return a point out of the grid.
        if (posX > 5) {
            posX = 5;
        }

        if (posY > 11) {
            posY = 11;
        }

        return new Point(posX, posY);
    }

    public void winLineAt(int line) {
        if (line <= 12) {
            mShouldShowWinLine = true;
            mWinLine = line;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mBoardWidth = getMeasuredWidth() - (getMeasuredWidth() % 6);
        mBoardHeight = getMeasuredHeight() - (getMeasuredHeight() % 12);
        mWidthOffset = 0;
        mHeightOffset = 0;

        mBlockSize = Math.min(mBoardWidth / 6,  mBoardHeight / 12);
        if (mBlockSize == (mBoardWidth / 6)) {
            mHeightOffset = (mBoardHeight - (mBlockSize * 12)) / 2;
            mBoardHeight = mBlockSize * 12;
        } else {
            mWidthOffset = (mBoardWidth - (mBlockSize * 6)) / 2;
            mBoardWidth = mBlockSize * 6;
        }

        if (mHeightOffset < 0 || mWidthOffset < 0) {
            throw new RuntimeException("Offsets are negative!");
        }

        mHeightOffset += 8;
        mWidthOffset += 8;

        mBoardRect.set(mWidthOffset, mHeightOffset, mBoardWidth + mWidthOffset, mBoardHeight + mHeightOffset);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Add background image
        canvas.drawRect(mBoardRect, mBoardPaint);
        if (mTrainerBitmap != null) {
            canvas.drawBitmap(mTrainerBitmap, null, mBoardRect, null);
        }

        if (mShouldAnimatingUp) {
            int bitmapBlockSize = (int)(BoardResources.getBlockHeights() * ((float)mRisingAnimationCounter / mNumOfTotalFrames));
            mRisingAnimationOffset = (int)(mBlockSize * (bitmapBlockSize / (float)BoardResources.getBlockHeights()));
        }

        // Create grid
        if (mBlocks != null) {
            for (int i = mBlocks.length - 1; i >= 0; i--) {
                for (int j = mBlocks[i].length - 1; j >= 0; j--) {
                    int x = (j * mBlockSize) + mWidthOffset;
                    int y = (i * mBlockSize) + mHeightOffset;

                    if (doesStatusAllowAnimation()) {
                        y -= mRisingAnimationOffset;
                    }

                    if (!mBlocks[i][j].isBlockEmpty()) {

                        if (mBlocks[i][j].hasMatched) {                                                                                                 // Handle matched blocks
                            mBlockRect.set(x, y, x + mBlockSize, y + mBlockSize);

                            if (mBlocks[i][j].matchInvertedAnimationCount > 0) {
                                drawMatchingBlock(canvas, mBlocks[i][j], mBlockRect, true, 0);
                                mBlocks[i][j].matchInvertedAnimationCount--;
                            } else if (mBlocks[i][j].delayMatchAnimationCount > 0) {
                                drawMatchingBlock(canvas, mBlocks[i][j], mBlockRect, false, 0);
                                mBlocks[i][j].delayMatchAnimationCount--;
                            } else if (mBlocks[i][j].matchPopAnimationCount < ANIMATION_MATCH_POP_FRAMES_NEEDED) {
                                drawMatchingBlock(canvas, mBlocks[i][j], mBlockRect, false, mBlocks[i][j].matchPopAnimationCount);
                                mBlocks[i][j].matchPopAnimationCount++;
                            } else if (mBlocks[i][j].clearMatchCount > 0) {
                                drawMatchingBlock(canvas, mBlocks[i][j], mBlockRect, false, ANIMATION_MATCH_POP_FRAMES_NEEDED + 1);
                                mBlocks[i][j].clearMatchCount--;
                                if (!mBlocks[i][j].hasPopped) {
                                    if (mListener != null) {
                                        mListener.blockIsPopping(mBlocks[i][j].popPosition, mBlocks[i][j].matchTotalCount);
                                    }
                                    mBlocks[i][j].hasPopped = true;
                                }

                            } else {
                                drawMatchingBlock(canvas, mBlocks[i][j], mBlockRect, false, ANIMATION_MATCH_POP_FRAMES_NEEDED + 1);
                                if (mListener != null) {
                                    mListener.blockFinishedMatchAnimation(i, j);
                                }
                                mBlocks[i][j].clear();
                            }
                        } else if (mBlocks[i][j].isBeingSwitched) {                                                                                     // Handle Switching blocks
                            int switchAnimationOffset = (int)(mBlockSize - ((mBlocks[i][j].switchAnimationCount / (float)ANIMATION_SWITCH_FRAMES_NEEDED) * mBlockSize));
                            if (mBlocks[i][j].leftRightAnimationDirection == 1) {                                                                       // left block animation
                                mBlockRect.set(x + switchAnimationOffset, y, x + mBlockSize + switchAnimationOffset, y + mBlockSize);
                                drawBlock(canvas, mBlocks[i][j], mBlockRect, 1);
                            } else {                                                                                                                    // right block animation
                                mBlockRect.set(x - switchAnimationOffset, y, x + mBlockSize - switchAnimationOffset, y + mBlockSize);
                                drawBlock(canvas, mBlocks[i][j], mBlockRect, 1);
                            }

                            mBlocks[i][j].switchAnimationCount++;

                            if (mBlocks[i][j].switchAnimationCount >= ANIMATION_SWITCH_FRAMES_NEEDED) {
                                mBlocks[i][j].stopSwitchAnimation();
                            }

                        } else if (mBlocks[i][j].isAnimatingDown) {                                                                                       // Handle falling blocks
                            boolean blockNeedsToSwap = false;
                            if (mBlocks[i][j].downAnimatingCount >= ANIMATION_FALLING_FRAMES_NEEDED) {
                                if (i < mBlocks.length - 1 && (mBlocks[i + 1][j].isBlockEmpty() || mBlocks[i + 1][j].isAnimatingDown) && !mBlocks[i + 1][j].isBeingSwitched && !mBlocks[i + 1][j].hasMatched) {
                                    blockNeedsToSwap = true;
                                } else {
                                    if (mBlocks[i][j].canCombo) {
                                        mBlocks[i][j].removeComboFlagOnNextFrame = true;
                                    }
                                    mBlocks[i][j].isAnimatingDown = false;
                                }
                            } else {
                                mBlocks[i][j].downAnimatingCount++;
                            }

                            int fallingAnimationOffset = (int)(((ANIMATION_FALLING_FRAMES_NEEDED - mBlocks[i][j].downAnimatingCount) / (float) ANIMATION_FALLING_FRAMES_NEEDED) * mBlockSize);
                            mBlockRect.set(x, y - fallingAnimationOffset, x + mBlockSize, y + mBlockSize - fallingAnimationOffset);
                            drawBlock(canvas, mBlocks[i][j], mBlockRect, 1);

                            if (blockNeedsToSwap) {
                                mBlocks[i][j].startFaillingAnimation();
                                if (mListener != null) {
                                    mListener.needsBlockSwap(mBlocks[i][j], mBlocks[i + 1][j]);
                                }
                            }

                        } else {
                            // Handle normal blocks
                            if (mBlocks[i][j].removeComboFlagOnNextFrame) {
                                mBlocks[i][j].removeComboFlagOnNextFrame = false;
                                mBlocks[i][j].canCombo = false;
                            }

                            mBlockRect.set(x, y, x + mBlockSize, y + mBlockSize);
                            drawBlock(canvas, mBlocks[i][j], mBlockRect, 1);
                        }
                    } else { // Handle blanks that are being switched
                        if (mBlocks[i][j].isBeingSwitched) {
                            mBlocks[i][j].switchAnimationCount++;

                            if (mBlocks[i][j].switchAnimationCount >= ANIMATION_SWITCH_FRAMES_NEEDED) {
                                mBlocks[i][j].stopSwitchAnimation();
                            }
                        }
                    }
                }
            }
        }

        // Draw new blocks coming in
        if (doesStatusAllowAnimation()) {
            int y = (12 * mBlockSize) + mHeightOffset;
            float bitmapRation = ((float)mRisingAnimationOffset) / mBlockSize;

            for (int i = 0; i < mNewRowBlocks.length; i++) {
                int x = (i * mBlockSize) + mWidthOffset;
                mBlockRect.set(x, y - mRisingAnimationOffset, x + mBlockSize, y);
                drawBlock(canvas, mNewRowBlocks[i], mBlockRect, bitmapRation);
            }
        }

        // Create block switcher
        if (mBlockSwitcher != null) {
            Point leftBlock = mBlockSwitcher.getLeftBlock();
            int x = (leftBlock.x * mBlockSize) + mWidthOffset;
            int y = (leftBlock.y * mBlockSize) + mHeightOffset;

            if (doesStatusAllowAnimation()) {
                y -= mRisingAnimationOffset;
            }

            canvas.drawLine(x, y, x + (mBlockSize / 4), y, mBlockSwitcherPaint);
            canvas.drawLine(x, y, x, y + (mBlockSize / 4), mBlockSwitcherPaint);
            canvas.drawLine(x, y + (3 * mBlockSize / 4), x, y + mBlockSize, mBlockSwitcherPaint);
            canvas.drawLine(x, y + mBlockSize, x + (mBlockSize / 4), y + mBlockSize, mBlockSwitcherPaint);

            canvas.drawLine(x + (3 * mBlockSize / 4), y, x + (5 * mBlockSize / 4), y, mBlockSwitcherPaint);
            canvas.drawLine(x + mBlockSize, y, x + mBlockSize, y + (mBlockSize / 4), mBlockSwitcherPaint);
            canvas.drawLine(x + mBlockSize, y + (3 * mBlockSize / 4), x + mBlockSize, y + mBlockSize, mBlockSwitcherPaint);
            canvas.drawLine(x + (3 * mBlockSize / 4), y + mBlockSize, x + (5 * mBlockSize / 4), y + mBlockSize, mBlockSwitcherPaint);

            canvas.drawLine(x + (7 * mBlockSize / 4), y, x + (2 * mBlockSize), y, mBlockSwitcherPaint);
            canvas.drawLine(x + (2 * mBlockSize), y, x + (2 * mBlockSize), y + (mBlockSize / 4), mBlockSwitcherPaint);
            canvas.drawLine(x + (2 * mBlockSize), y + (3 * mBlockSize / 4), x + (2 * mBlockSize), y + mBlockSize, mBlockSwitcherPaint);
            canvas.drawLine(x + (7 * mBlockSize / 4), y + mBlockSize, x + (2 * mBlockSize), y + mBlockSize, mBlockSwitcherPaint);

        }

        // Create win line
        if (mShouldShowWinLine) {
            int y = (mWinLine != 0 && doesStatusAllowAnimation()) ? mWinLine * mBlockSize - mRisingAnimationOffset : mWinLine * mBlockSize;
            canvas.drawLine(mWidthOffset, y + mHeightOffset, mBoardWidth + mWidthOffset, y + mHeightOffset, mBlockSwitcherPaint);
        }

        // Create board border
        drawRectBoarder(canvas, mWidthOffset, mHeightOffset, mBoardWidth + mWidthOffset, mBoardHeight + mHeightOffset);

        if (mShouldAnimatingUp) {
            mRisingAnimationCounter++;
        }
    }

    private boolean doesStatusAllowAnimation() {
        return (mCurrentStatus == GameStatus.Running) || (mCurrentStatus == GameStatus.Panic);
    }

    private void drawBlock(Canvas canvas, Block block, Rect position, float heightRatio) {
        if (block.getBlockType() != Block.BlockType.EMPTY) {
            if (heightRatio > 1) {
                heightRatio = 1;
            } else if (heightRatio < 0) {
                heightRatio = 0;
            }

            Bitmap currentBitmap;
            if (heightRatio == 1) {
                currentBitmap = BoardResources.getNormalBlock(block.getBlockType());
            } else {
                currentBitmap = BoardResources.getDarkBlock(block.getBlockType());
            }
            mBlockRectScale.set(0, 0, currentBitmap.getWidth(), (int)(currentBitmap.getHeight() * heightRatio));
            canvas.drawBitmap(currentBitmap, mBlockRectScale, position, null);
        }
    }

    private void drawMatchingBlock(Canvas canvas, Block block, Rect position, boolean isInverted, int animationCount) {
        Bitmap currentBitmap;

        if (isInverted) {
            currentBitmap = BoardResources.getInvertedBlock(block.getBlockType());
        } else {
            currentBitmap = BoardResources.getPopAnimationBlock(block.getBlockType(), animationCount);
        }

        canvas.drawBitmap(currentBitmap, null, position, null);
    }

    private void drawRectBoarder(Canvas canvas, int startingX, int startingY, int endingX, int endingY) {
        int[] colors = mContext.getResources().getIntArray(R.array.blue_border_colors);
        int lightColor = colors[0];
        int darkColor = colors[1];
        mBoardBoarderPaint.setStrokeWidth(2);

        for (int i = 0; i < 16; i++) {
            int red = (int)(((Color.red(lightColor) - Color.red(darkColor)) * i) / 15f);
            int green = (int)(((Color.green(lightColor) - Color.green(darkColor)) * i) / 15f);
            int blue = (int)(((Color.blue(lightColor) - Color.blue(darkColor)) * i) / 15f);

            int newRed = Color.red(lightColor) - red;
            int newGreen = Color.green(lightColor) - green;
            int newBlue = Color.blue(lightColor) - blue;

            newRed = (newRed < 0) ? 0 : newRed;
            newGreen = (newGreen < 0) ? 0 : newGreen;
            newBlue = (newBlue < 0) ? 0 : newBlue;

            int newColor = Color.rgb(newRed, newGreen, newBlue);

            mBoardBoarderPaint.setColor(newColor);
            canvas.drawRect(startingX + i, startingY + i, endingX - i, endingY - i, mBoardBoarderPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = MotionEventCompat.getActionIndex(event);
                final float x = MotionEventCompat.getX(event, pointerIndex);
                final float y = MotionEventCompat.getY(event, pointerIndex);
                final Point p = getGridCoordinatesOffXY(x, y + mRisingAnimationOffset);

                // Remember where we started (for dragging)
                mLastTouch = p;
                // Save the ID of this pointer (for dragging)
                mActivePointerId = MotionEventCompat.getPointerId(event, 0);
                if (mBlockSwitcher.areCoordinatesInSwitcher(p.x, p.y)) {
                    mBlockSwitcher.switcherIsBeingMoved = true;
                    mLeftBlockSwitcherIsBeingMoved = mBlockSwitcher.areCoordinatesInLeftBlock(p.x, p.y);
                    mRightBlockSwitcherIsBeingMoved = mBlockSwitcher.areCoordinatesInRightBlock(p.x, p.y);
                }

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // Find the index of the active pointer and fetch its position
                final int pointerIndex =
                        MotionEventCompat.findPointerIndex(event, mActivePointerId);

                final float x = MotionEventCompat.getX(event, pointerIndex);
                final float y = MotionEventCompat.getY(event, pointerIndex);
                final Point newP = getGridCoordinatesOffXY(x, y + mRisingAnimationOffset);

                if (mBlockSwitcher.switcherIsBeingMoved) {
                    boolean isReallyMoving = true;
                    if (mLeftBlockSwitcherIsBeingMoved && !mBlockSwitcher.areCoordinatesInLeftBlock(newP.x, newP.y)) {
                        if (newP.x > 4) {
                            newP.x = 4;
                            isReallyMoving = false;
                        }

                        if ((mCurrentStatus == GameStatus.Running || mCurrentStatus == GameStatus.Panic) && newP.y == 0) {
                            newP.y = 1;
                            isReallyMoving = false;
                        }

                        mBlockSwitcher.setLeftBlock(newP);

                        if (mListener != null && isReallyMoving) {
                            mListener.switchBlockMoved();
                        }
                    } else if (mRightBlockSwitcherIsBeingMoved && !mBlockSwitcher.areCoordinatesInRightBlock(newP.x, newP.y)) {
                        if (newP.x < 1) {
                            newP.x = 1;
                            isReallyMoving = false;
                        }

                        if ((mCurrentStatus == GameStatus.Running || mCurrentStatus == GameStatus.Panic) && newP.y == 0) {
                            newP.y = 1;
                            isReallyMoving = false;
                        }

                        mBlockSwitcher.setRightBlock(newP);
                        if (mListener != null && isReallyMoving) {
                            mListener.switchBlockMoved();
                        }
                    }

                    // Remember this touch position for the next move event
                    mLastTouch = newP;
                }

                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;

                if (!mBlockSwitcher.switcherIsBeingMoved) {
                    float currentX = event.getX();
                    float currentY = event.getY();
                    Point p = getGridCoordinatesOffXY(currentX, currentY + mRisingAnimationOffset);
                    int deltaY = mLastTouch.y - p.y;

                    if (deltaY >= MIN_SWIPE_DISTANCE) {
                        if (mListener != null) {
                            mListener.addNewRow();
                        }
                    }
                    else if (p.x == mLastTouch.x && p.y == mLastTouch.y) {
                        Point leftBlockSwitch = mBlockSwitcher.getLeftBlock();
                        tryToSwitch(leftBlockSwitch);
                    }

                }

                mBlockSwitcher.switcherIsBeingMoved = false;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                mBlockSwitcher.switcherIsBeingMoved = false;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {

                final int pointerIndex = MotionEventCompat.getActionIndex(event);

                float currentX = MotionEventCompat.getX(event, pointerIndex);
                float currentY = MotionEventCompat.getY(event, pointerIndex);
                Point p = getGridCoordinatesOffXY(currentX, currentY + mRisingAnimationOffset);

                if (p.x == mLastTouchPointer.x && p.y == mLastTouchPointer.y) {
                    Point leftBlockSwitch = mBlockSwitcher.getLeftBlock();
                    tryToSwitch(leftBlockSwitch);
                }

                final int pointerId = MotionEventCompat.getPointerId(event, pointerIndex);

                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mActivePointerId = MotionEventCompat.getPointerId(event, newPointerIndex);
                    mBlockSwitcher.switcherIsBeingMoved = false;
                }

                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {
                final int pointerIndex = MotionEventCompat.getActionIndex(event);
                final float x = MotionEventCompat.getX(event, pointerIndex);
                final float y = MotionEventCompat.getY(event, pointerIndex);
                mLastTouchPointer = getGridCoordinatesOffXY(x, y + mRisingAnimationOffset);
                break;
            }
        }
        return true;
    }

    private void tryToSwitch(Point leftBlockSwitch) {
        try {
            if (!mBlocks[leftBlockSwitch.y - 1][leftBlockSwitch.x].isAnimatingDown && !mBlocks[leftBlockSwitch.y - 1][leftBlockSwitch.x + 1].isAnimatingDown && tryToSwitchHelper(leftBlockSwitch)) {
                startSwitchAnimation(leftBlockSwitch);
                if (mListener != null) {
                    mListener.switchBlock(leftBlockSwitch);
                }
            }
        } catch(Exception e) {
            if (tryToSwitchHelper(leftBlockSwitch)) {
                startSwitchAnimation(leftBlockSwitch);
                if (mListener != null) {
                    mListener.switchBlock(leftBlockSwitch);
                }
            }
        }
    }

    private boolean tryToSwitchHelper(Point leftBlockSwitch) {
        if (!mBlocks[leftBlockSwitch.y][leftBlockSwitch.x].isAnimatingDown && !mBlocks[leftBlockSwitch.y][leftBlockSwitch.x + 1].isAnimatingDown) {
            if (!mBlocks[leftBlockSwitch.y][leftBlockSwitch.x].hasMatched && !mBlocks[leftBlockSwitch.y][leftBlockSwitch.x + 1].hasMatched){
                return true;
            }
        }

        return false;
    }


    private void startSwitchAnimation(Point leftBlockSwitch) {
        mBlocks[leftBlockSwitch.y][leftBlockSwitch.x].startSwitchAnimation(0);
        mBlocks[leftBlockSwitch.y][leftBlockSwitch.x + 1].startSwitchAnimation(1);
    }

    public void setBoardListener(BoardListener listener) {
        mListener = listener;
    }

}

interface BoardListener {
    void switchBlock(Point switcherLeftBlock);
    void addNewRow();
    void blockFinishedMatchAnimation(int row, int column);
    void blockIsPopping(int position, int total);
    void needsBlockSwap(Block b1, Block b2);
    void switchBlockMoved();
}
