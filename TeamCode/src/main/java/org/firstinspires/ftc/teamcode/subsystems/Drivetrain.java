package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Subsystem;

public class Drivetrain extends Subsystem {

    private DcMotor leftFront, leftBack, rightFront, rightBack;

    private double sensitivity = 1;

    public Drivetrain(Gamepad gamepad1, Gamepad gamepad2, Telemetry tele, HardwareMap map) {
        super(gamepad1, gamepad2, tele, map);
    }

    @Override
    public void init() {
        leftFront = map.get(DcMotor.class, "leftf");
        leftBack = map.get(DcMotor.class, "leftb");
        rightFront = map.get(DcMotor.class, "rightf");
        rightBack = map.get(DcMotor.class, "rightb");

        leftFront.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        tele.addLine("Drivetrain Initialized!");
    }

    @Override
    public void operate(ElapsedTime runtime) {
        /* SENS */
        sensitivity += gamepad1.right_trigger - gamepad1.left_trigger * 0.01;
        if(gamepad1.left_bumper) sensitivity = 0.5;
        else if(gamepad1.right_bumper) sensitivity = 1.0;

        /* DRIVETRAIN */
        double r = Math.hypot(gamepad1.left_stick_x, gamepad1.left_stick_y);
        double robotAngle = Math.atan2(gamepad1.left_stick_y, gamepad1.left_stick_x) - Math.PI / 4;
        double rightX = -gamepad1.right_stick_x;
        final double v1 = r * Math.sin(robotAngle) + rightX;
        final double v2 = r * Math.sin(robotAngle) - rightX;
        final double v3 = r * Math.cos(robotAngle) + rightX;
        final double v4 = r * Math.cos(robotAngle) - rightX;

        float scale = (float) (1f / Math.sqrt(2)) * (float) sensitivity;

        leftFront.setPower(v1 * scale);
        rightFront.setPower(v2 * scale);
        leftBack.setPower(v3 * scale);
        rightBack.setPower(v4 * scale);
    }

}
