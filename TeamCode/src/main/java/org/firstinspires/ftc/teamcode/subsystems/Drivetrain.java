package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class Drivetrain extends Subsystem {

    public DcMotor leftFront, leftBack, rightFront, rightBack;

    private double sensitivity = 1;

    private final double TICKS_PER_REV = 1440, WHEEL_DIAM_IN = 75.0 / 25.4, GEAR_RATIO = 4.0 / 3.0, WHEEL_CIRCUM = WHEEL_DIAM_IN * Math.PI;
    private final double RAW_TO_IN = (GEAR_RATIO * WHEEL_CIRCUM) / (TICKS_PER_REV), IN_TO_RAW = (TICKS_PER_REV) / (GEAR_RATIO * WHEEL_CIRCUM);

    private final double K_TURN = 50.0;

    BNO055IMU imu;
    Orientation lastAngles = new Orientation();
    double globalAngle, lastAngle;

    boolean hasReset = false, isGlobal = true, globalButton = false;

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

        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.mode                = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled      = false;

        imu = map.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);

        tele.addLine("Drivetrain Initialized!");
    }

    @Override
    public void operate(ElapsedTime runtime) {
        if(!hasReset) {
            resetAngle();
            hasReset = true;
        }

        /* SENS */
        sensitivity += gamepad1.right_trigger - gamepad1.left_trigger * 0.01;
        if(gamepad1.left_bumper) sensitivity = 0.5;
        else if(gamepad1.right_bumper) sensitivity = 1.0;

        drive(gamepad1.left_stick_x, -gamepad1.left_stick_y, gamepad1.right_stick_x, (float) sensitivity);

        if(gamepad1.x && !globalButton) {
            isGlobal = !isGlobal;
        }

        if(gamepad1.b) resetAngle();

        globalButton = gamepad1.x;
    }

    public void drive(double x, double z, double turn, double speedFactor) {
        /* DRIVETRAIN */
        double r = Math.hypot(x, -z);
        double robotAngle = Math.atan2(-z, x) - (Math.PI / 4);

        double error;
        if(Math.abs(turn) > 0) {
            lastAngle = getAngle();
        } else {
            error = lastAngle - getAngle();
            turn += (error / lastAngle) / K_TURN;
        }

        double rightX = -turn;
        final double v1 = r * Math.sin(robotAngle) + rightX;
        final double v2 = r * Math.sin(robotAngle) - rightX;
        final double v3 = r * Math.cos(robotAngle) + rightX;
        final double v4 = r * Math.cos(robotAngle) - rightX;

        float scale = (float) ((float) (1f / Math.sqrt(2)) * speedFactor);

        leftFront.setPower(v1 * scale);
        rightFront.setPower(v2 * scale);
        leftBack.setPower(v3 * scale);
        rightBack.setPower(v4 * scale);

        tele.addData("Angle", getAngle() * (180 / Math.PI));
        tele.addData("Speed", v1);
    }

    private void resetAngle() {
        lastAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        globalAngle = 0;
    }

    private double getAngle() {
        // We experimentally determined the Z axis is the axis we want to use for heading angle.
        // We have to process the angle because the imu works in euler angles so the Z axis is
        // returned as 0 to +180 or 0 to -180 rolling back to -179 or +179 when rotation passes
        // 180 degrees. We detect this transition and track the total cumulative angle of rotation.

        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        double deltaAngle = angles.firstAngle - lastAngles.firstAngle;

        if (deltaAngle < -180)
            deltaAngle += 360;
        else if (deltaAngle > 180)
            deltaAngle -= 360;

        globalAngle += deltaAngle;

        lastAngles = angles;

        return globalAngle * (Math.PI / 180); // to radians
    }

    public double getRBDistance() {
        return Math.abs(rightBack.getCurrentPosition() * (RAW_TO_IN / 4));
    }

    public double getRFDistance() {
        return Math.abs(rightFront.getCurrentPosition() * (RAW_TO_IN / 4));
    }

    public double getLBDistance() {
        return Math.abs(leftBack.getCurrentPosition() * (RAW_TO_IN / 4));
    }

    public double getLFDistance() {
        return Math.abs(leftFront.getCurrentPosition() * (RAW_TO_IN / 4));
    }

    public double convertRawToIn(double raw) {
        return raw * RAW_TO_IN;
    }

    public double convertInToRaw(double in) {
        return in * IN_TO_RAW;
    }

    public void resetEncoders() {
        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

}
