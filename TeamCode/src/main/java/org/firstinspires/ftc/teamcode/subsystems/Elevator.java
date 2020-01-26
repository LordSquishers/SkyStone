package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Elevator extends Subsystem {

    private DcMotor left, right;

    public Elevator(Gamepad gamepad1, Gamepad gamepad2, Telemetry tele, HardwareMap map) {
        super(gamepad1, gamepad2, tele, map);
    }

    @Override
    public void init() {
        left = map.get(DcMotor.class, "eleft");
        right = map.get(DcMotor.class, "eright");

        right.setDirection(DcMotorSimple.Direction.REVERSE);

        tele.addLine("Elevator Initialized!");
    }

    @Override
    public void operate(ElapsedTime runtime) {
        float speed = -gamepad2.left_stick_y;

        if(gamepad2.left_stick_button) speed /= 2;

        setElevatorMotors(speed);
    }

    public void operate(float speed) {
        setElevatorMotors(speed);
    }

    private void setElevatorMotors(float speed) {
        left.setPower(speed);
        right.setPower(speed);
    }
}
