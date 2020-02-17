package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystems.CheeskakeIntake;
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Elevator;
import org.firstinspires.ftc.teamcode.subsystems.Foundation;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Subsystem;

import java.util.ArrayList;
import java.util.List;

@TeleOp(name = "Robot")
public class Robot extends LinearOpMode {

    private Subsystem drivetrain, elevator, intake, foundation, cheesKakeIntake;
    private ElapsedTime runtime = new ElapsedTime();
    private List<Subsystem> subsystems = new ArrayList<>();

    @Override
    public void runOpMode() throws InterruptedException {
        initSubsystems();
        telemetry.addLine("Robot ready!");
        telemetry.update();

        waitForStart();
        runtime.reset();

        while(!isStopRequested()) {

            for(Subsystem s: subsystems) {
                s.operate(runtime);
            }

            telemetry.addData("Time", runtime.seconds()); // Add time left instead? Not yet
            telemetry.update();
        }

    }

    public void initSubsystems() {
        drivetrain = new Drivetrain(gamepad1, gamepad2, telemetry, hardwareMap);
        subsystems.add(drivetrain);

        elevator = new Elevator(gamepad1, gamepad2, telemetry, hardwareMap);
        subsystems.add(elevator);

        intake = new Intake(gamepad1, gamepad2, telemetry, hardwareMap);
        subsystems.add(intake);

//        cheesKakeIntake = new CheeskakeIntake(gamepad1, gamepad2, telemetry, hardwareMap);
//        subsystems.add(cheesKakeIntake);

        foundation = new Foundation(gamepad1, gamepad2, telemetry, hardwareMap);
        subsystems.add(foundation);
    }
}
