package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

@Disabled
@Autonomous(name = "Prototype Autonomous 2.0 Bakup")
public class PrototypeAuto2Backup extends OpMode {

    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;
    private static final double TICKS_PER_REVOLUTION = 383.6;
    private static final double WHEEL_DIAMETER = 4;
    private static final double TICKS_PER_INCH = (TICKS_PER_REVOLUTION / (WHEEL_DIAMETER * Math.PI));
    private static final double TICKS_PER_DEGREE = ((TICKS_PER_INCH * 16 * Math.PI) / 360);
    private static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Quad";
    private static final String LABEL_SECOND_ELEMENT = "Single";
    private static final String VUFORIA_KEY = "Af7azD3/////AAABmVHvSAkLQ0TVtUCkrp4gYxVB3LN+B36QaQwW89MAEuMNaGdhauo7pLRQz4o+z9ewyPm2BzQmdQ7GskkDiPTXO1WWNzBfktE91Gd6HI5hvKTYqbLKFa44lxJMA24vIwXzQuP1qsfVO0aIYQaw3vON7OkFZtm3+aul0VHS1mJ/xz4LZ9Tynv28MTlw9LxjhMrLsCrlpDHJGRlidcLWP/k9/LIjozFUuAwJQDdfw/dFeBqY5OHyGAVI09LJ3hCwnpSB/tJK1cJBXdvq8YbdHvZnTPvRRzEP1aI4b32XyOLdoyvM7KlvFD03eJrQHPaFBpgmwFAoyxZhyUdHPCsSRSx31mdLp/Fr7b043iy7a5TWEDN2";

    private DcMotor leftCenter;
    private DcMotor leftFront = null;
    private DcMotor leftBack = null;
    private DcMotor rightCenter;
    private DcMotor rightFront = null;
    private DcMotor rightBack = null;
    private DcMotor intake = null;
    private DcMotor lift = null;
    private Servo grabber = null;
    private Servo leftDumper = null;
    private Servo rightDumper = null;
    private double rightDumperStartPosition = .8;
    private double leftDumperStartPosition = .2;
    boolean HasRouteRun = false;
    double StartTime = getRuntime();
    double CurrentTime = getRuntime();
    String routeName = null;
    int StartPositionLeft;
    int StartPositionRight;

    @Override
    public void init() {

        initDrive();
        initVuforia();
        initTfod();

        if (tfod != null) {
            tfod.activate();
            tfod.setZoom(2.5, 1.78);
        } else {
            telemetry.addData("TFOD", "TFOD is (null)!!");
            telemetry.update();
        }
        msStuckDetectLoop = 30500;
    }

    @Override
    public void init_loop() {
        List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
        if (updatedRecognitions != null) {
            int i = 0;
            for (Recognition recognition : updatedRecognitions) {
                telemetry.addData("# Object Detected", updatedRecognitions.size());

                if(recognition.getLabel() == "Single") {
                    routeName = "Single";
                }
                else if(recognition.getLabel() == "Quad") {
                    routeName = "Quad";
                }
                telemetry.addData("route" , routeName);
                telemetry.addData(">", "Press Play to start op mode");
                telemetry.update();
            }
        }
    }

    @Override
    public void start() {
        List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
        if (updatedRecognitions != null) {
            for (Recognition recognition : updatedRecognitions) {
                if(recognition.getLabel() == "Single") {
                    routeName = "Single";
                    telemetry.addData("Running Single Route", 0);
                    telemetry.update();
                }
                else if(recognition.getLabel() == "Quad") {
                    routeName = "Quad";
                    telemetry.addData("Running Quad Route", 0);
                    telemetry.update();

                }
                else {
                    telemetry.addData("Running Normal Route", 0);
                    telemetry.update();
                }

                if (tfod != null) {
                    tfod.shutdown();
                }
            }
        }
    }
    @Override
    public void loop() {
        CurrentTime = getRuntime();
        if (routeName == "Single" && HasRouteRun == false) {
            StartTime = getRuntime();
            RunSingleRoute();
            HasRouteRun = true;
        }
        else if (routeName == "Quad" && HasRouteRun == false) {
            StartTime = getRuntime();
            RunQuadRoute();
            HasRouteRun = true;
        }
        else if (HasRouteRun == false){
            StartTime = getRuntime();
            telemetry.addData("Running Normal Route", 0);
            telemetry.update();
            RunNormalRoute();
            HasRouteRun = true;
        }
    }

    private void initDrive() {

        leftCenter = hardwareMap.get(DcMotor.class, "left_center");
        leftBack = hardwareMap.get(DcMotor.class, "left_back");
        leftFront = hardwareMap.get(DcMotor.class, "left_front");
        rightCenter = hardwareMap.get(DcMotor.class, "right_center");
        rightBack = hardwareMap.get(DcMotor.class, "right_back");
        rightFront = hardwareMap.get(DcMotor.class, "right_front");
        intake = hardwareMap.get(DcMotor.class, "intake");
        lift = hardwareMap.get(DcMotor.class, "lift");
        grabber = hardwareMap.get(Servo.class, "grabber");
        leftDumper = hardwareMap.get(Servo.class, "left_dumper");
        rightDumper = hardwareMap.get(Servo.class, "right_dumper");

        leftCenter.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        leftFront.setDirection(DcMotor.Direction.FORWARD);
        rightCenter.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        intake.setDirection(DcMotor.Direction.REVERSE);
        lift.setDirection(DcMotor.Direction.FORWARD);

        leftCenter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightCenter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftCenter.setTargetPosition(0);
        rightCenter.setTargetPosition(0);
        leftCenter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightCenter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftCenter.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightCenter.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftCenter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightCenter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

    }

    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.8f;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT);
        tfod.activate();
    }

    private void RunSingleRoute() {
        leftCenter.setTargetPosition((int) (TICKS_PER_INCH * 130));
        leftCenter.setPower(.5);
        rightCenter.setTargetPosition((int) (TICKS_PER_INCH * 135));
        rightCenter.setPower(.5);
        telemetry.addData("Running Single Route", 0);
        telemetry.addData("TicksRemaining(Left)", leftCenter.getTargetPosition() - leftCenter.getCurrentPosition());
        telemetry.addData("TicksRemaining(Right)", rightCenter.getTargetPosition() - rightCenter.getCurrentPosition());
        telemetry.update();
    }
    private void RunQuadRoute() {
        leftCenter.setTargetPosition((int) (TICKS_PER_INCH * 101));
        leftCenter.setPower(.5);
        rightCenter.setTargetPosition((int) (TICKS_PER_INCH * 101));
        rightCenter.setPower(.5);
        telemetry.addData("Running Quad Route", 0);
        telemetry.addData("TicksRemaining(Left)", leftCenter.getTargetPosition() - leftCenter.getCurrentPosition());
        telemetry.addData("TicksRemaining(Right)", rightCenter.getTargetPosition() - rightCenter.getCurrentPosition());
        telemetry.update();
    }
    private void RunNormalRoute() {
        CurrentTime = getRuntime();
        while(CurrentTime - StartTime >= 0 && CurrentTime - StartTime < 5) {
            CurrentTime = getRuntime();
            leftCenter.setTargetPosition((int) (TICKS_PER_INCH * 75));
            leftCenter.setPower(.5);
            rightCenter.setTargetPosition((int) (TICKS_PER_INCH * 75));
            rightCenter.setPower(.5);
            telemetry.addData("Running Normal Route", 0);
            telemetry.addData("TicksRemaining(Left)", leftCenter.getTargetPosition() - leftCenter.getCurrentPosition());
            telemetry.addData("TicksRemaining(Right)", rightCenter.getTargetPosition() - rightCenter.getCurrentPosition());
            telemetry.addData("Status", "2nd phase of Normal route");
            telemetry.addData("Position", "L: %d, R: %d", leftCenter.getCurrentPosition(), rightCenter.getCurrentPosition());
            telemetry.update();
        }
        CurrentTime = getRuntime();
        while ((CurrentTime - StartTime >= 3.5) && (CurrentTime - StartTime < 3.6)) {
            CurrentTime = getRuntime();
            StartPositionLeft = leftCenter.getCurrentPosition();
            StartPositionRight = rightCenter.getCurrentPosition();
            telemetry.addData("Status", "2nd phase of Normal route");
            telemetry.addData("Position", "L: %d, R: %d", leftCenter.getCurrentPosition(), rightCenter.getCurrentPosition());
            telemetry.update();
        }
        CurrentTime = getRuntime();
        while ((CurrentTime - StartTime >= 3.6) && (CurrentTime - StartTime < 5)) {
            CurrentTime = getRuntime();
            leftCenter.setTargetPosition((int) ((StartPositionLeft) + (TICKS_PER_INCH * -8)));
            leftCenter.setPower(-.5);
            rightCenter.setTargetPosition((int) ((StartPositionRight) + (TICKS_PER_INCH * -8)));
            rightCenter.setPower(-.5);
            telemetry.addData("Status", "3rd phase of Normal route");
            telemetry.addData("TicksRemaining(Left)", leftCenter.getTargetPosition() - leftCenter.getCurrentPosition());
            telemetry.addData("TicksRemaining(Right)", rightCenter.getTargetPosition() - rightCenter.getCurrentPosition());
            telemetry.addData("Position", "L: %d, R: %d", leftCenter.getCurrentPosition(), rightCenter.getCurrentPosition());
            telemetry.update();
        }
        while ((CurrentTime - StartTime >= 5) && (CurrentTime - StartTime < 5.1)) {
            CurrentTime = getRuntime();
            StartPositionLeft = leftCenter.getCurrentPosition();
            StartPositionRight = rightCenter.getCurrentPosition();
            telemetry.addData("Status", "4th phase of Normal route");
            telemetry.addData("Position", "L: %d, R: %d", leftCenter.getCurrentPosition(), rightCenter.getCurrentPosition());
            telemetry.update();
        }
        while ((CurrentTime - StartTime >= 5.1) && (CurrentTime - StartTime < 6)) {
            CurrentTime = getRuntime();
            leftCenter.setTargetPosition((int) ((StartPositionLeft) + (TICKS_PER_DEGREE * 30)));
            leftCenter.setPower(.5);
            rightCenter.setTargetPosition((int) ((StartPositionRight) + (TICKS_PER_DEGREE * 30)));
            rightCenter.setPower(-.5);
            telemetry.addData("Status", "5th phase of Normal route");
            telemetry.addData("Position", "L: %d, R: %d", leftCenter.getCurrentPosition(), rightCenter.getCurrentPosition());
            telemetry.update();
        }
        while ((CurrentTime - StartTime >= 6) && (CurrentTime - StartTime < 6.1)) {
            CurrentTime = getRuntime();
            StartPositionLeft = leftCenter.getCurrentPosition();
            StartPositionRight = rightCenter.getCurrentPosition();
            telemetry.addData("Status", "6th phase of Normal route");
            telemetry.addData("Position", "L: %d, R: %d", leftCenter.getCurrentPosition(), rightCenter.getCurrentPosition());
            telemetry.update();
        }
        while ((CurrentTime - StartTime >= 6.1) && (CurrentTime - StartTime < 6.8)) {
            CurrentTime = getRuntime();
            leftCenter.setTargetPosition((int) ((StartPositionLeft) + (TICKS_PER_INCH * 18)));
            leftCenter.setPower(.5);
            rightCenter.setTargetPosition((int) ((StartPositionRight) + (TICKS_PER_DEGREE * 18)));
            rightCenter.setPower(.5);
            telemetry.addData("Status", "7th phase of Normal route");
            telemetry.addData("Position", "L: %d, R: %d", leftCenter.getCurrentPosition(), rightCenter.getCurrentPosition());
            telemetry.update();
        }
        while ((CurrentTime - StartTime >= 6.8) && (CurrentTime - StartTime < 6.9)) {
            CurrentTime = getRuntime();
            StartPositionLeft = leftCenter.getCurrentPosition();
            StartPositionRight = rightCenter.getCurrentPosition();
            telemetry.addData("Status", "8th phase of Normal route");
            telemetry.addData("Position", "L: %d, R: %d", leftCenter.getCurrentPosition(), rightCenter.getCurrentPosition());
            telemetry.update();
        }
        while ((CurrentTime - StartTime >= 6.9) && (CurrentTime - StartTime < 7.5)) {
            CurrentTime = getRuntime();
            leftCenter.setTargetPosition((int) ((StartPositionLeft) + (TICKS_PER_DEGREE * -30)));
            leftCenter.setPower(.5);
            rightCenter.setTargetPosition((int) ((StartPositionRight) + (TICKS_PER_DEGREE * -30)));
            rightCenter.setPower(-.5);
            telemetry.addData("Status", "9th phase of Normal route");
            telemetry.addData("Position", "L: %d, R: %d", leftCenter.getCurrentPosition(), rightCenter.getCurrentPosition());
            telemetry.update();
        }
        while ((CurrentTime - StartTime >= 7.5) && (CurrentTime - StartTime < 7.6)) {
            CurrentTime = getRuntime();
            StartPositionLeft = leftCenter.getCurrentPosition();
            StartPositionRight = rightCenter.getCurrentPosition();
            telemetry.addData("Status", "10th phase of Normal route");
            telemetry.addData("Position", "L: %d, R: %d", leftCenter.getCurrentPosition(), rightCenter.getCurrentPosition());
            telemetry.update();
        }
        while ((CurrentTime - StartTime >= 7.6) && (CurrentTime - StartTime < 8.5)) {
            CurrentTime = getRuntime();
            leftCenter.setTargetPosition((int) ((StartPositionLeft) + (TICKS_PER_INCH * 10)));
            leftCenter.setPower(.5);
            rightCenter.setTargetPosition((int) ((StartPositionRight) + (TICKS_PER_INCH * 10)));
            rightCenter.setPower(.5);
            telemetry.addData("Status", "11th phase of Normal route");
            telemetry.addData("Position", "L: %d, R: %d", leftCenter.getCurrentPosition(), rightCenter.getCurrentPosition());
            telemetry.update();
        }
        while ((CurrentTime - StartTime >= 8.5) && (CurrentTime - StartTime < 9)) {
            CurrentTime = getRuntime();
            StartPositionLeft = leftCenter.getCurrentPosition();
            StartPositionRight = rightCenter.getCurrentPosition();
            rightDumper.setPosition(.25);
            leftDumper.setPosition(.75);
            telemetry.addData("Status", "12th phase of Normal route");
            telemetry.addData("Position", "L: %d, R: %d", leftCenter.getCurrentPosition(), rightCenter.getCurrentPosition());
            telemetry.update();
        }
        while ((CurrentTime - StartTime >= 9) && (CurrentTime - StartTime < 10)) {
            CurrentTime = getRuntime();
            leftCenter.setTargetPosition((int) ((StartPositionLeft) + (TICKS_PER_INCH * -50)));
            leftCenter.setPower(-.5);
            rightCenter.setTargetPosition((int) ((StartPositionRight) + (TICKS_PER_INCH * -50)));
            rightCenter.setPower(-.5);
            telemetry.addData("Status", "13th phase of Normal route");
            telemetry.addData("Position", "L: %d, R: %d", leftCenter.getCurrentPosition(), rightCenter.getCurrentPosition());
            telemetry.update();
        }
        while ((CurrentTime - StartTime >= 10) && (CurrentTime - StartTime < 10.5)) {
            CurrentTime = getRuntime();
            StartPositionLeft = leftCenter.getCurrentPosition();
            StartPositionRight = rightCenter.getCurrentPosition();
            rightDumper.setPosition(rightDumperStartPosition);
            leftDumper.setPosition(leftDumperStartPosition);
            telemetry.addData("Status", "14th phase of Normal route");
            telemetry.addData("Position", "L: %d, R: %d", leftCenter.getCurrentPosition(), rightCenter.getCurrentPosition());
            telemetry.update();
        }
        stop();
    }
}