package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.mechanisms.MecanumRobotDrive;


@TeleOp
public class MecanumRobotOrientedOpMode extends OpMode {

    MecanumRobotDrive drive = new MecanumRobotDrive();


    double forward, strafe, rotate, intake;
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

<<<<<<< HEAD


       if (gamepad1.dpadUpWasPressed())

        }
=======
>>>>>>> 2400b7b6ab925f5e5ffd2a1f141f73fb1d1e61c4
    }
}




