package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;

@TeleOp
public class BasicServoOpMode extends OpMode {
    private Servo servoPos;
    private CRServo servoRot;

    @Override
    public void init() {
        servoPos = hardwareMap.get(Servo.class, "PosServo");
        servoRot = hardwareMap.get(CRServo.class, "ConServo");

        servoPos.setPosition(0.0);
        servoRot.setPower(0);
    }

    @Override
    public void loop() {
        // press A to stop, B for full speed
        if (gamepad1.aWasPressed()) servoPos.setPosition(0.0);
        if (gamepad1.bWasPressed()) servoPos.setPosition(1.0);

        // Right trigger to throttle
        servoRot.setPower(gamepad1.right_trigger);

        telemetry.addData("pos", servoPos.getPosition());
        telemetry.addData("rot power", gamepad1.right_trigger);
    }
}
