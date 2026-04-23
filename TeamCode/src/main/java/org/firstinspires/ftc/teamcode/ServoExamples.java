package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.TestBench;

@TeleOp
public class ServoExamples extends OpMode {
    TestBench bench = new TestBench();

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
