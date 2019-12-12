package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.util.ElapsedTime;

public abstract class AutoCommand {

    public ElapsedTime commandTime;
    public double totalTime;

    public AutoCommand(ElapsedTime commandTime, double totalTime) {
        this.commandTime = commandTime;
        this.totalTime = totalTime;
    }

    public abstract void execute(ElapsedTime runtime);
}
