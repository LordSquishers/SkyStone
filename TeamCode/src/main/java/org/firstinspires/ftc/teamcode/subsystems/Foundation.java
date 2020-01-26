package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Foundation extends Subsystem {

    private Servo left, right;

    private final float
            LEFT_OPEN = 0.33f, LEFT_CLOSE = 1.0f,
            RIGHT_OPEN = 0.66f, RIGHT_CLOSE = -1.0f;

    float leftPos = LEFT_OPEN, rightPos = RIGHT_OPEN;

    public Foundation(Gamepad gamepad1, Gamepad gamepad2, Telemetry tele, HardwareMap map) {
        super(gamepad1, gamepad2, tele, map);
    }

    @Override
    public void init() {
        left = map.get(Servo.class, "leftf");
        right = map.get(Servo.class, "rightf");

        tele.addLine("Foundation Initialized!");
    }

    @Override
    public void operate(ElapsedTime runtime) {
        if(gamepad2.right_trigger != 0) {
            leftPos = LEFT_CLOSE;
            rightPos = RIGHT_CLOSE;
        } else if(gamepad2.left_trigger != 0) {
            leftPos = LEFT_OPEN;
            rightPos = RIGHT_OPEN;
        }

        left.setPosition(leftPos);
        right.setPosition(rightPos);

        tele.addData("Left Pos", leftPos);

    }

    public void operate(boolean isOpen) {
        float leftPos = LEFT_OPEN, rightPos = RIGHT_OPEN;

        if(!isOpen) {
            leftPos = LEFT_CLOSE;
            rightPos = RIGHT_CLOSE;
        } else {
            leftPos = LEFT_OPEN;
            rightPos = RIGHT_OPEN;
        }

        left.setPosition(leftPos);
        right.setPosition(rightPos);

    }
}
