package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
@TeleOp
public class Calculator extends OpMode {
    int number1 = 0;
    int number2 = 0;
    int result = 0;

    @Override
    public void init() {
    }
    @Override
    public void loop() {
        boolean aButton = gamepad1.a;
        boolean bButton = gamepad1.b;

        telemetry.addData("first number", number1);
        telemetry.addData("second number", number2);

        if (gamepad1.leftTriggerWasPressed()) {
            number1 -= 1;
        } else if (gamepad1.leftBumperWasPressed()) {
            number1 += 1;
        }
        if (gamepad1.rightTriggerWasPressed()) {
            number2 -= 1;
        } else if (gamepad1.rightBumperWasPressed()) {
            number2 += 1;
        }
        if (aButton) {
            result = number1 + number2;
            telemetry.addData("result", result);
        } else if (bButton) {
            number1 = 0;
            number2 = 0;
            result = 0;
        }
    }
}