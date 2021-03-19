package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "TankBlocks", group = "")
public class SampleBlocksTankDrive extends LinearOpMode {

    private DcMotor right;
    private DcMotor left;

    /**
     * This function is executed when this Op Mode is selected from the Driver Station.
     */
    @Override
    public void runOpMode() {
        double Dead;
        float Right_Stick_Y;
        float Left_Stick_Y;

        right = hardwareMap.get(DcMotor.class, "right");
        left = hardwareMap.get(DcMotor.class, "left");

        waitForStart();
        while (opModeIsActive()) {
            Dead = 0.5;
            Right_Stick_Y = gamepad1.right_stick_y;
            Left_Stick_Y = gamepad1.left_stick_y;
            if (Right_Stick_Y > Dead || Right_Stick_Y < -Dead) {
                right.setPower(Right_Stick_Y);
            } else {
                right.setPower(0);
            }
            if (Left_Stick_Y > Dead || Left_Stick_Y < -Dead) {
                left.setPower(-Left_Stick_Y);
            } else {
                left.setPower(0);
            }
        }
    }
}
