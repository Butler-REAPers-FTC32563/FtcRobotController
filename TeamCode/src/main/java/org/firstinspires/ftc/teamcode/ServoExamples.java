package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.TestBenchServo;

@TeleOp
public class ServoExamples extends OpMode {
    TestBenchServo bench = new TestBenchServo();

    double leftTrigger, rightTrigger;

    public void init() {
        bench.init(hardwareMap);
       leftTrigger = 0;
       rightTrigger = 0;
    }

    public void loop() {
        leftTrigger = gamepad1.left_trigger;
        rightTrigger = gamepad1.right_trigger;

        bench.setServoPos(leftTrigger);
        bench.setServoRot(rightTrigger);
    }
}
