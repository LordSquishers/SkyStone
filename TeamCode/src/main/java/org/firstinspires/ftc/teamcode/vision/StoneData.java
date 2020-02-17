package org.firstinspires.ftc.teamcode.vision;

public class StoneData {

    private String label;
    private double topBound, bottomBound, leftBound, rightBound;

    public StoneData(String label, double topBound, double bottomBound, double leftBound, double rightBound) {
        this.topBound = topBound;
        this.bottomBound = bottomBound;
        this.leftBound = leftBound;
        this.rightBound = rightBound;

        this.label = label;
    }

    public String getLabel() {
        return label;
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
