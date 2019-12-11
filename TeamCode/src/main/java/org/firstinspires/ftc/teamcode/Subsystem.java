package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public abstract class Subsystem {

    protected Gamepad gamepad1, gamepad2;
    protected Telemetry tele;
    protected HardwareMap map;

    public Subsystem(Gamepad gamepad1, Gamepad gamepad2, Telemetry tele, HardwareMap map) {
        this.gamepad1 = gamepad1;
        this.gamepad2 = gamepad2;
        this.tele = tele;
        this.map = map;

        init();
    }

    public abstract void init();

    public abstract void operate(ElapsedTime runtime);

}
