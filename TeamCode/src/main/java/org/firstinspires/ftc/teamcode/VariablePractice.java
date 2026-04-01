package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class VariablePractice extends OpMode {
    @Override
    public void init() {
        int teamNumber = 32563; // int = round number
        double motorSpeed = 0.75; // double = Number with decimals
        boolean clawClosed = true; // boolean = true or false
        String teamName = "REAPERS"; // String = words
        int motorAngle = 163;

        telemetry.addData("Team Number", teamNumber);
        telemetry.addData("motor speed", motorSpeed);
        telemetry.addData("claw closed", clawClosed);
        telemetry.addData("team name", teamName);
        telemetry.addData("motor angle", motorAngle);

    }

    @Override
    public void loop() {

    }
}
