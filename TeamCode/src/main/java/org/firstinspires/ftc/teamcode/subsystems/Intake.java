package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Subsystem;

public class Intake extends Subsystem {

    private CRServo left, right;

    private boolean shouldDisable;
    private double speed = 0.0;

    public Intake(Gamepad gamepad1, Gamepad gamepad2, Telemetry tele, HardwareMap map) {
        super(gamepad1, gamepad2, tele, map);
    }

    @Override
    public void init() {
        left = map.get(CRServo.class, "intakel");
        right = map.get(CRServo.class, "intaker");

        right.setDirection(DcMotorSimple.Direction.REVERSE);

        tele.addLine("Intake Initialized!");
    }

    @Override
    public void operate(ElapsedTime runtime) {
        if(gamepad2.a) {
            speed = 1.0;
        }

        if(gamepad2.y) {
            speed = -1.0;

            shouldDisable = true;
        }

        if(shouldDisable && !gamepad2.y) {
            speed = 0.0;
            shouldDisable = false;
        }

        setIntakePower(speed);
        tele.addData("Intake", speed);
    }

    private void setIntakePower(double speed) {
        left.setPower(speed);
        right.setPower(speed);
    }
}
