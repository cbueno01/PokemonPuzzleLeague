package com.productions.gizzmoo.pokemonpuzzleleague;

import android.graphics.Point;

import java.io.Serializable;

/**
 * Created by Chrystian on 1/30/2018.
 */

public class SwitchBlocks implements Serializable {

    // 0 based
    private int leftRow;
    private int leftColumn;
    private int rightRow;
    private int rightColumn;

    public boolean switcherIsBeingMoved;

    public SwitchBlocks(int leftC, int leftR, int rightC, int rightR) {
        this.setCoordinates(leftC, leftR, rightC, rightR);
        switcherIsBeingMoved = false;
    }

    public Point getLeftBlock() {
        return new Point(leftColumn, leftRow);
    }

    private boolean isSwitcherStable() {
        return (leftRow == rightRow) && (leftColumn == rightColumn - 1);
    }

    public void setLeftBlock(Point p) {
        if (p.x < 0 || p.x > 4 || p.y < 0 || p.y > 11) {
            throw new RuntimeException("Point is out of bound");
        }

        setCoordinates(p.x, p.y, p.x + 1, p.y);
    }

    public void setRightBlock(Point p) {
        if (p.x < 1 || p.x > 5 || p.y < 0 || p.y > 11) {
            throw new RuntimeException("Point is out of bound");
        }

        setCoordinates(p.x - 1, p.y, p.x, p.y);
    }

    public void setCoordinates(int leftC, int leftR, int rightC, int rightR) {
        leftRow = leftR;
        leftColumn = leftC;
        rightRow = rightR;
        rightColumn = rightC;

        if (!isSwitcherStable()) {
            throw new RuntimeException("Switcher is not stable");
        }
    }

    public boolean areCoordinatesInSwitcher(int x, int y) {
        return areCoordinatesInLeftBlock(x, y) || areCoordinatesInRightBlock(x, y);
    }

    public boolean areCoordinatesInLeftBlock(int x, int y) {
        return leftColumn == x && leftRow == y;
    }

    public boolean areCoordinatesInRightBlock(int x, int y) {
        return rightColumn == x && rightRow == y;
    }

    public void moveUp() {
        if (leftRow > 0 && rightRow > 0) {
            leftRow--;
            rightRow--;
        }
    }

    public void moveDown() {
        if (leftRow < 11 && rightRow < 11) {
            leftRow++;
            rightRow++;
        }
    }

    public boolean isAtTop() {
        return leftRow <= 1;
    }
}
