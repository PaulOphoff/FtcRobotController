package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "NormalBlocks", group = "")
public class SampleBlocksNormalDrive extends LinearOpMode {

    private DcMotor right;
    private DcMotor left;

    /**
     * This function is executed when this Op Mode is selected from the Driver Station.
     */
    @Override
    public void runOpMode() {
        double Dead;
        float Right_Stick_X;
        float Left_Stick_Y;

        right = hardwareMap.get(DcMotor.class, "right");
        left = hardwareMap.get(DcMotor.class, "left");

        waitForStart();
        while (opModeIsActive()) {
            Dead = 0.5;
            Right_Stick_X = gamepad1.right_stick_x;
            Left_Stick_Y = gamepad1.left_stick_y;
            if (Left_Stick_Y > Dead || Left_Stick_Y < -Dead) {
                right.setPower(Left_Stick_Y);
                left.setPower(-Left_Stick_Y);
            } else if (Right_Stick_X > Dead || Right_Stick_X < -Dead) {
                right.setPower(Right_Stick_X);
                left.setPower(Right_Stick_X);
            } else {
                right.setPower(0);
                left.setPower(0);
            }
        }
    }
}
