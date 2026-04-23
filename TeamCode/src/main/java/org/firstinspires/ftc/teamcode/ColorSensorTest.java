package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.TestBench;

@TeleOp
public class ColorSensorTest extends OpMode {

    TestBench bench = new TestBench();

    TestBench.DetectedColor detectedColor;

    @Override
    public void init() {
        bench.init(hardwareMap);
    }

    @Override
    public void loop() {

        detectedColor = bench.getDetectedColor(telemetry);
        telemetry.addData("Detected Color", detectedColor);
        telemetry.update();

    }
}
