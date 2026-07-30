package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class ifPractice extends OpMode {
    @Override
    public void init() {

    }

    @Override
    public void loop() {
        double leftY = gamepad2.left_stick_y;
        double motorSpeed = 11;
        boolean aButton = gamepad1.a;


        if (leftY < 0.1 && leftY > -0.1) {
            telemetry.addData("Left Stick", "In Dead Zone");

        if (!aButton) {
            motorSpeed = motorSpeed /2;
        }
        }

        telemetry.addData("Left Stick Value", leftY);
        telemetry.addData("motor speed", motorSpeed);
    }
}

/*

AND = && "if (leftY < 0.5 && leftY > 0) {"
or = || "if (leftY < 0 || rightY < 0) {"
NOT = ! "if (!clawClosed) {"

 */


