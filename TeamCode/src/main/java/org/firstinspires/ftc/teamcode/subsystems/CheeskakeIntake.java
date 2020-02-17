package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class CheeskakeIntake extends Subsystem {

    private Servo deployServo, stoneServo;

    private boolean isDeployed = false;

    public CheeskakeIntake(Gamepad gamepad1, Gamepad gamepad2, Telemetry tele, HardwareMap map) {
        super(gamepad1, gamepad2, tele, map);
    }

    @Override
    public void init() {
        deployServo = map.get(Servo.class, "deploy");
        stoneServo = map.get(Servo.class, "stone");
    }

    @Override
    public void operate(ElapsedTime runtime) {
        if(gamepad2.y && !isDeployed) isDeployed = true;
        if(gamepad2.y && isDeployed) isDeployed = false;

        deployServo.setPosition(isDeployed ? 1.0 : 0.0);
        stoneServo.setPosition(gamepad2.a ? 1.0 : 0.0);

    }
}
