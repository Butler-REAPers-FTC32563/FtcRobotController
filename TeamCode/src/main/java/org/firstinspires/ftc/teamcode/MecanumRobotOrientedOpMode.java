package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.MecanumRobotDrive;

@TeleOp
public class MecanumRobotOrientedOpMode extends OpMode {
    MecanumRobotDrive drive = new MecanumRobotDrive();

    double forward, strafe, rotate, intake, ClawOpen;
    boolean intakeOn;

    @Override
    public void init() {
        drive.init(hardwareMap);
    }

    @Override
    public void loop() {
        forward = -gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        rotate = gamepad1.right_stick_x;

        if (gamepad1.dpad_up) {
            drive.setConServo(1.0);
        }
        else if (gamepad1.dpad_down) {
            drive.setConServo(-1.0);
        }
        else {
            drive.setConServo(0);
        }
        if(gamepad1.aWasReleased()) {
            intakeOn = !intakeOn;
        }

        intake = intakeOn ? 1 : 0;






        drive.drive(forward, strafe, rotate, intake);

        telemetry.addData("Forward", forward);
        telemetry.addData("Strafe", strafe);
        telemetry.addData("Rotate", rotate);
        telemetry.addData("FrontLeft RPM", drive.frontLeftRPM());
        telemetry.addData("FrontRight RPM", drive.frontRightRPM());
        telemetry.addData("BackLeft RPM", drive.backLeftRPM());
        telemetry.addData("BackRight RPM", drive.backRightRPM());
        telemetry.addData("IntakeOn", intakeOn);
        telemetry.update();

        if (gamepad1.dpad_left) {
            drive.setClawPos(0);
        }
        else if (gamepad1.dpad_right) {
            drive.setClawPos(1.0);
        }
    }
}


