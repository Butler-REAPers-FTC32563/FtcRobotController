package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.mechanisms.TestBench1;

public class DcMotorPractice extends OpMode {

    TestBench1 bench = new TestBench1();

    @Override
    public void init() {
        bench.init(hardwareMap);

    }

    @Override
    public void loop() {
        bench.setMotorSpeed(0.5);

    }
}
