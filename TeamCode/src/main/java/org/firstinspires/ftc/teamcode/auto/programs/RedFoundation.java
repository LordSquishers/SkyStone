/* Copyright (c) 2019 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.auto.programs;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Elevator;
import org.firstinspires.ftc.teamcode.subsystems.Foundation;
import org.firstinspires.ftc.teamcode.subsystems.Intake;

@Autonomous(name="Red Foundation", group ="Auto")
public class RedFoundation extends LinearOpMode {

    private Drivetrain drivetrain;
    private Elevator elevator;
    private Foundation foundation;
    private Intake intake;

    private ElapsedTime period = new ElapsedTime();

    @Override
    public void runOpMode() {
        drivetrain = new Drivetrain(gamepad1, gamepad2, telemetry, hardwareMap);
        elevator = new Elevator(gamepad1, gamepad2, telemetry, hardwareMap);
        foundation = new Foundation(gamepad1, gamepad2, telemetry, hardwareMap);
        intake = new Intake(gamepad1, gamepad2, telemetry, hardwareMap);

        waitForStart();
        period.reset();

        // unhook
        // 5 inches forward
        // 48 inches right to foundation
        // hook
        // sleep 1 sec
        // 48 inches left to wall
        // unhook
        // move backwards 51 inches

        foundation.operate(true);
        drivetrain.driveWithEncoders(-5, false, 0.5);
        drivetrain.driveWithEncoders(48, true, 0.5);

        foundation.operate(false);
        sleep(1000);
        drivetrain.driveWithEncoders(-48, true, 0.5);

        foundation.operate(true);
        sleep(500);
        drivetrain.driveWithEncoders(51, false, 0.5);

    }
}
