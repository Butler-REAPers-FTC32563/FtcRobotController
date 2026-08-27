package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.mechanisms.TestBench;

@TeleOp
public class ImuPractice extends OpMode {
    TestBench bench = new TestBench();
    private DcMotor motor;

    @Override
    public void init() {
        bench.init(hardwareMap);
        motor = hardwareMap.get(DcMotor.class, "motor");
    }

    @Override
    public void loop() {
        double heading = bench.getHeading(AngleUnit.DEGREES);
        telemetry.addData("Heading", heading);
        if (heading < 0.5 && heading > -0.5) {
            motor.setPower(0);
        } else if (heading >= 0.5) {
            motor.setPower(0.5);
        } else if (heading <= -0.5) {
            motor.setPower(-0.5);
            
        }
        telemetry.update();
    }
}
