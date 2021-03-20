/* Copyright (c) 2017 FIRST. All rights reserved.
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

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.CRServo;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * This file contains an example of an iterative (Non-Linear) "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * The names of OpModes appear on the menu of the FTC Driver Station.
 * When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all iterative OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="Blackout drive")
public class Blackout_Driving extends OpMode {
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotorEx leftCenter = null;
    private DcMotorEx leftFront = null;
    private DcMotorEx leftBack = null;
    private DcMotorEx rightCenter = null;
    private DcMotorEx rightFront = null;
    private DcMotorEx rightBack = null;
    private DcMotorEx intake = null;
    private DcMotorEx lift = null;
    private Servo grabber = null;
    private Servo leftDumper = null;
    private Servo rightDumper = null;
    private double rightDumperStartPosition = .8;
    private double leftDumperStartPosition = .2;
    private final double grabberClosedPosition = .10;
    private final double grabberOpenPosition = .50;
    boolean isGrabberOpen = true;
    private TouchSensor limitSwitch;


    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).

        leftBack = hardwareMap.get(DcMotorEx.class, "left_back");
        leftFront = hardwareMap.get(DcMotorEx.class, "left_front");
        rightCenter = hardwareMap.get(DcMotorEx.class, "right_center");
        rightBack = hardwareMap.get(DcMotorEx.class, "right_back");
        rightFront = hardwareMap.get(DcMotorEx.class, "right_front");
        intake = hardwareMap.get(DcMotorEx.class, "intake");
        lift = hardwareMap.get(DcMotorEx.class, "lift");
        grabber = hardwareMap.get(Servo.class, "grabber");
        leftDumper = hardwareMap.get(Servo.class, "left_dumper");
        rightDumper = hardwareMap.get(Servo.class, "right_dumper");
        leftCenter = hardwareMap.get(DcMotorEx.class, "left_center");
        limitSwitch = hardwareMap.touchSensor.get("limit_Switch");

        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        leftCenter.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        leftFront.setDirection(DcMotor.Direction.FORWARD);
        rightCenter.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        intake.setDirection(DcMotor.Direction.REVERSE);
        lift.setDirection(DcMotor.Direction.FORWARD);


        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift.setTargetPosition(0);
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        rightDumper.setPosition(rightDumperStartPosition);
        leftDumper.setPosition(leftDumperStartPosition);
        telemetry.addData("position away from target", lift.getTargetPosition() - lift.getCurrentPosition());

        // Tell the driver that initialization is complete.

        telemetry.addData("position", lift.getCurrentPosition());
        telemetry.addData("is at target", !lift.isBusy());
        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {

        if(limitSwitch.isPressed()){
            lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        } else {
            lift.setTargetPosition(1000);
            lift.setVelocity(200);
        }
    }
    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        runtime.reset();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        // Setup a variable for each drive wheel to save power level for telemetry
        double leftPower;
        double rightPower;

        // Choose to drive using either Tank Mode, or POV Mode
        // Comment out the method that's not used.  The default below is POV.

        // POV Mode uses left stick to go forward, and right stick to turn.
        // - This uses basic math to combine motions and is easier to drive straight.

        double drive;
        double turn;

        if(gamepad1.left_stick_y < .01 || gamepad1.left_stick_y > -.01) {
            drive = -gamepad1.left_stick_y;
            turn = gamepad1.right_stick_x;
        } else {
            drive = -gamepad2.left_stick_y;
            turn = gamepad2.right_stick_x;
        }
        leftPower  = Range.clip(drive - turn, -1.0, 1.0);
        rightPower = Range.clip(drive + turn, -1.0, 1.0);
        boolean intake_button = gamepad1.a;
        boolean intake_stop_button = gamepad1.b;
        if(intake_button == true){
            intake.setPower(1);
        }
        if(intake_stop_button){
            intake.setPower(0);
        }
        double lift_button = gamepad1.right_trigger;
        boolean lift_stop_button = gamepad1.right_bumper;
        double lift_down_button = gamepad1.left_trigger;
        if(lift_button > .1){
            lift.setTargetPosition(0);
            lift.setVelocity(513);

        }
        else if(lift_down_button > .1){
            lift.setTargetPosition(-757);
            lift.setVelocity(513);
        }
        else if(lift_stop_button == true){
            lift.setTargetPosition(-234);
            lift.setVelocity(537);

        }
        boolean grabber_button = gamepad1.y;
        boolean grabber_return_button = gamepad1.x;
        if(grabber_button == true && isGrabberOpen == false){
            grabber.setPosition(grabberOpenPosition);
            isGrabberOpen = true;
        }
        else if(grabber_return_button == true && isGrabberOpen == true){
            grabber.setPosition(grabberClosedPosition);
            isGrabberOpen = false;
        }
        boolean Dumper_button = gamepad1.dpad_up;
        boolean Dumper_return_button = gamepad1.dpad_down;
        boolean Dumper_halfway_button = gamepad1.dpad_right;
        if(Dumper_button == true){
            rightDumper.setPosition(.25);
            leftDumper.setPosition(.75);
        }
        else if(Dumper_return_button == true){
            rightDumper.setPosition(rightDumperStartPosition);
            leftDumper.setPosition(leftDumperStartPosition);
        }
        /*if(rangeSensor.rawUltrasonic() < 5 ) {
            leftBack.setPower(0);
            leftCenter
            left
        }*/
        // Tank Mode uses one stick to control each wheel.
        // - This requires no math, but it is hard to drive forward slowly and keep straight.
        // leftPower  = -gamepad1.left_stick_y ;
        // rightPower = -gamepad1.right_stick_y ;

        // Send calculated power to wheels
        leftFront.setPower(leftPower);
        leftCenter.setPower(leftPower);
        leftBack.setPower(leftPower);
        rightFront.setPower(rightPower);
        rightCenter.setPower(rightPower);
        rightBack.setPower(rightPower);

        // Show the elapsed game time and wheel power.
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors", "left (%.2f), right (%.2f)", leftPower, rightPower);
        telemetry.addData("lift position", lift.getCurrentPosition());
        telemetry.update();
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }
}