package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.mechanisms.TestBench;

@TeleOp
public class DcMotorPractice extends OpMode {

    TestBench bench = new TestBench();

    @Override
    public void init() {
        bench.init(hardwareMap);

    }

    @Override
    public void loop() {
        double motorSpeed = gamepad1.left_stick_y;

        bench.setMotorSpeed(motorSpeed);

        if (gamepad1.a) {
            bench.setMotorBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
        else if (gamepad1.b) {
            bench.setMotorBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        }

        return motorSpeed;
    }

}

