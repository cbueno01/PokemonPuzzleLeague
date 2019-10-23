package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame;

import android.graphics.Point;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Chrystian on 2/10/2018.
 */

public class Block implements Serializable, Comparable<Block> {

    private BlockType type;
    private int xCoor;
    private int yCoor;

    public boolean isBeingSwitched;
    public int switchAnimationCount;
    public Direction leftRightAnimationDirection;

    public boolean isAnimatingDown;
    public int downAnimatingCount;

    public boolean hasMatched;
    public int delayMatchAnimationCount;
    public int matchInvertedAnimationCount;
    public int matchPopAnimationCount;
    public int clearMatchCount;
    public int matchTotalCount;

    public boolean hasPopped;
    public int popPosition;

    public boolean canCombo;
    public boolean removeComboFlagOnNextFrame;

    public Block(int t, int x, int y) {
        switch (t) {
            case 1:
                type = BlockType.LEAF;
                break;
            case 2:
                type = BlockType.FIRE;
                break;
            case 3:
                type = BlockType.HEART;
                break;
            case 4:
                type = BlockType.WATER;
                break;
            case 5:
                type = BlockType.COIN;
                break;
            case 6:
                type = BlockType.TRAINER;
                break;
            case 7:
                type = BlockType.DIAMOND;
                break;
            default:
                type = BlockType.EMPTY;
        }

        xCoor = x;
        yCoor = y;

        resetBlockValues();
    }

    public void clear() {
        type = BlockType.EMPTY;
        resetBlockValues();
    }

    private void resetBlockValues() {
        isBeingSwitched = false;
        switchAnimationCount = 0;
        leftRightAnimationDirection = Direction.None;

        isAnimatingDown = false;
        downAnimatingCount = 0;
        hasMatched = false;
        matchPopAnimationCount = 0;
        matchInvertedAnimationCount = 0;
        delayMatchAnimationCount = 0;
        clearMatchCount = 0;
        matchTotalCount = 0;
        hasPopped = false;
        popPosition = 0;

        canCombo = false;
        removeComboFlagOnNextFrame = false;
    }

    @Override
    public int compareTo(@NonNull Block otherBlock) {
        Point p1 = getCoords();
        Point p2 = otherBlock.getCoords();

        if (p1.y == p2.y) {
            return p1.x - p2.x;
        } else {
            return p1.y - p2.y;
        }
    }

    public void changeCoords(int newX, int newY) {
        xCoor = newX;
        yCoor = newY;
    }

    public Point getCoords() {
        return new Point(xCoor, yCoor);
    }

    public boolean isBlockEmpty() {
        return type == BlockType.EMPTY;
    }

    public BlockType getBlockType() {
        return type;
    }

    public void startFallingAnimation() {
        downAnimatingCount = 0;
        isAnimatingDown = true;
    }

    public void stopFallingAnimation() {
        downAnimatingCount = 0;
        isAnimatingDown = false;
    }

    public void startSwitchAnimation(Direction direction) {
        isBeingSwitched = true;
        switchAnimationCount = 1;
        leftRightAnimationDirection = direction;
    }

    public void stopSwitchAnimation() {
        isBeingSwitched = false;
        switchAnimationCount = 0;
        leftRightAnimationDirection = Direction.None;
    }

    public void blockMatched(int delayedMatchAnimationFrames, int matchInvertedAnimationFrames, int clearMatchFrames, int position, int totalNumOfBlockMatches) {
        hasMatched = true;
        matchPopAnimationCount = 0;
        delayMatchAnimationCount = delayedMatchAnimationFrames;
        matchInvertedAnimationCount = matchInvertedAnimationFrames;
        clearMatchCount = clearMatchFrames;
        popPosition = position;
        matchTotalCount = totalNumOfBlockMatches;
    }

    public boolean canInteract() {
        return !isBlockEmpty() && !isAnimating();
    }

    public boolean isAnimating() {
        return isAnimatingDown || hasMatched || isBeingSwitched;
    }

    public void incrementSwitchAnimationFrame() {
        switchAnimationCount++;
    }

    public void incrementDownAnimationFrame() {
        downAnimatingCount++;
    }

    public void incrementPopAnimationFrame() {
        matchPopAnimationCount++;
    }

    public void decrementDelayedMatchAnimationFrame() {
        delayMatchAnimationCount--;
    }

    public void decrementInvertedAnimationFrame() {
        matchInvertedAnimationCount--;
    }

    public void decrementClearFrame() {
        clearMatchCount--;
    }

    public void blockPopped() {
        hasPopped = true;
    }

    public boolean isAnimatingLeft() {
        return leftRightAnimationDirection == Direction.Left;
    }

    public enum BlockType {
        EMPTY(0),
        LEAF(1),
        FIRE(2),
        HEART(3),
        WATER(4),
        COIN(5),
        TRAINER(6),
        DIAMOND(7);

        private final int value;

        BlockType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}
