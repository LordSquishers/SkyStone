package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.vision.StoneData;

import java.util.ArrayList;
import java.util.List;

import static org.firstinspires.ftc.teamcode.Constants.LABEL_FIRST_ELEMENT;
import static org.firstinspires.ftc.teamcode.Constants.LABEL_SECOND_ELEMENT;
import static org.firstinspires.ftc.teamcode.Constants.TFOD_MODEL_ASSET;
import static org.firstinspires.ftc.teamcode.Constants.VUFORIA_KEY;

public class Drivetrain extends Subsystem {

    public DcMotor leftFront, leftBack, rightFront, rightBack;
    public Servo capstoneServo;

    private double sensitivity = 1;

    private final double TICKS_PER_REV = 1440, WHEEL_DIAM_IN = 75.0 / 25.4, GEAR_RATIO = 4.0 / 3.0, WHEEL_CIRCUM = WHEEL_DIAM_IN * Math.PI;
    private final double RAW_TO_IN = (GEAR_RATIO * WHEEL_CIRCUM) / (TICKS_PER_REV * 50./72.), IN_TO_RAW = (TICKS_PER_REV * 50./72.) / (GEAR_RATIO * WHEEL_CIRCUM);

    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;
    private List<StoneData> stoneData = new ArrayList<>();

    BNO055IMU imu;
    Orientation lastAngles = new Orientation();
    double globalAngle;

    boolean hasReset = false, isGlobal = true, globalButton = false;

    public Drivetrain(Gamepad gamepad1, Gamepad gamepad2, Telemetry tele, HardwareMap map) {
        super(gamepad1, gamepad2, tele, map);
    }

    @Override
    public void init() {
        trackingSetup();

        leftFront = map.get(DcMotor.class, "leftf");
        leftBack = map.get(DcMotor.class, "leftb");
        rightFront = map.get(DcMotor.class, "rightf");
        rightBack = map.get(DcMotor.class, "rightb");

        capstoneServo = map.get(Servo.class, "cap");

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

        if(gamepad2.right_stick_button) {
            capstoneServo.setPosition(1.0);
        } else {
            capstoneServo.setPosition(0.5);
        }

        globalButton = gamepad1.x;
    }

    public void drive(double x, double z, double turn, double speedFactor) {
        /* DRIVETRAIN */
        double r = Math.hypot(x, -z);
        double robotAngle = Math.atan2(-z, x) - (Math.PI / 4) - getAngle();

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
        tele.addData("X", x);
        tele.addData("Z", z);
        tele.addData("Turn", turn);
        tele.addData("Global?", isGlobal);
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

    public void driveWithEncoders(double distance, boolean isHorizontal, double speed) {
        double targetFront = -distance * IN_TO_RAW;
        double targetBack = targetFront * (isHorizontal ? -1 : 1);

        targetFront *= isHorizontal ? (10./9.) : 1;
        targetBack *= isHorizontal ? (10./9.) : 1;

        // left diag = top left || b right
        // right diag = bottom left || t right
        resetEncoders();
        setRunToPosition();

        setFrontTarget(targetFront);
        setBackTarget(targetBack);
        setMotorPower(speed);

        while(Math.abs(leftFront.getCurrentPosition()) < Math.abs(targetBack) && Math.abs(leftBack.getCurrentPosition()) < Math.abs(targetFront)) {
            tele.addData("remaining", targetFront - leftFront.getCurrentPosition());
            tele.update();
        }

        setRunWithEncoders();
    }

    //TODO- implement TF
    public void driveWithScan(double distance, boolean isHorizontal, double speed) {
        double targetFront = -distance * IN_TO_RAW;
        double targetBack = targetFront * (isHorizontal ? -1 : 1);

        targetFront *= isHorizontal ? (10./9.) : 1;
        targetBack *= isHorizontal ? (10./9.) : 1;

        // left diag = top left || b right
        // right diag = bottom left || t right
        resetEncoders();
        setRunToPosition();

        setFrontTarget(targetFront);
        setBackTarget(targetBack);
        setMotorPower(speed);

        while(Math.abs(leftFront.getCurrentPosition()) < Math.abs(targetBack) && Math.abs(leftBack.getCurrentPosition()) < Math.abs(targetFront)) {
            findStones();

            for(StoneData stone: stoneData) {
                if(stone.getLabel() == LABEL_SECOND_ELEMENT) return;
            }

            tele.addData("remaining", targetFront - leftFront.getCurrentPosition());
            tele.update();
        }

        setRunWithEncoders();
    }

    public void setMotorPower(double power) {
        leftFront.setPower(power);
        rightFront.setPower(power);
        leftBack.setPower(power);
        rightBack.setPower(power);
    }

    public void setFrontTarget(double position) {
        leftFront.setTargetPosition((int) (position * IN_TO_RAW));
        rightFront.setTargetPosition((int) (position * IN_TO_RAW));
    }

    public void setBackTarget(double position) {
        rightBack.setTargetPosition((int) (position * IN_TO_RAW));
        leftBack.setTargetPosition((int) (position * IN_TO_RAW));
    }

    public void setRunToPosition() {
        leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void setRunWithEncoders() {
        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public double getRBDistance() {
        return Math.abs(rightBack.getCurrentPosition() * (RAW_TO_IN));
    }

    public double getRFDistance() {
        return Math.abs(rightFront.getCurrentPosition() * (RAW_TO_IN));
    }

    public double getLBDistance() {
        return Math.abs(leftBack.getCurrentPosition() * (RAW_TO_IN));
    }

    public double getLFDistance() {
        return Math.abs(leftFront.getCurrentPosition() * (RAW_TO_IN));
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

    private void findStones() {
        if (tfod != null) {
            // getUpdatedRecognitions() will return null if no new information is available since
            // the last time that call was made.
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
            if (updatedRecognitions != null) {
                tele.addData("# Object Detected", updatedRecognitions.size());
                // step through the list of recognitions and display boundary info.
                int i = 0;
                stoneData.clear();
                for (Recognition recognition : updatedRecognitions) {
                    tele.addData(String.format("label (%d)", i), recognition.getLabel());
                    tele.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                            recognition.getLeft(), recognition.getTop());
                    tele.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                            recognition.getRight(), recognition.getBottom());

                    stoneData.add(new StoneData(recognition.getLabel(), recognition.getTop(), recognition.getBottom(), recognition.getLeft(), recognition.getRight()));
                }
                tele.update();
            }
        }
    }

    private void trackingSetup() {
        initVuforia();

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            tele.addData("Sorry!", "This device is not compatible with TFOD");
        }

        if (tfod != null) {
            tfod.activate();
        }
    }

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = map.get(WebcamName.class, "Webcam 1");

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = map.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", map.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minimumConfidence = 0.8;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT);
    }

}
