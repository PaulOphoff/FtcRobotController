package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;


            /*  TODO Setup for a TeleOp program with the name ServoSample.java  */


@TeleOp(name = "ServoSample")
public class ServoSample {


            /*  TODO Here is the "class", in the class section of the program, you declare the electronic parts that you want to use in the program, so for this sample, we are using a servo  */

    public Servo servo;

            /*  TODO Here is the "Init" portion of a TeleOp program, In the Init portion, you initialize all the Motors, servos, and Sensors, and set them to their corresponding Configuration Names  */

    public void init() {

        servo = hardwareMap.get(Servo.class, "ServoConfigName");
    }

            /*  TODO Here is the "Loop" portion of a TeleOp program, In this section, the phone takes all the parts that are already declared, and figures out what to do with it.  */
            /*  TODO This is where all the declaration is done for, and now you need to tell the program what you want the Servo to do whenever you press a certain button  */
            /*  TODO I will use The button B in order to move the servo in one position, and the button A to move it to another position */

    public void loop() {

        if (gamepad1.b == true) {
            servo.setPosition(-1);
        }
        else if (gamepad1.a == true) {
            servo.setPosition(1);
        }
    }
}
