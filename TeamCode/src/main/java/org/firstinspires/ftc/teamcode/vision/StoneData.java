package org.firstinspires.ftc.teamcode.vision;

public class StoneData {

    private double topBound, bottomBound, leftBound, rightBound;

    public StoneData(double topBound, double bottomBound, double leftBound, double rightBound) {
        this.topBound = topBound;
        this.bottomBound = bottomBound;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
    }

    public double getTopBound() {
        return topBound;
    }

    public double getBottomBound() {
        return bottomBound;
    }

    public double getLeftBound() {
        return leftBound;
    }

    public double getRightBound() {
        return rightBound;
    }
}
