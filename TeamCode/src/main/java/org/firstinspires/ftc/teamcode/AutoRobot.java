package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.auto.AutoCommand;

import java.util.ArrayList;
import java.util.List;

@Autonomous(name = "Auto")
public class AutoRobot extends LinearOpMode {

    private List<AutoCommand> commands = new ArrayList<>();
    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() throws InterruptedException {
        addCommands();

        waitForStart();
        runtime.reset();
        while(!isStopRequested()) {
            for(AutoCommand c: commands) {
                c.execute(runtime);
            }
        }
    }

    public void addCommands() {

    }
}
