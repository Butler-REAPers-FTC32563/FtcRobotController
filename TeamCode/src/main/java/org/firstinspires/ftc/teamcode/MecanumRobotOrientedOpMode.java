package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.mechanisms.MecanumRobotDrive;

@TeleOp
public class MecanumRobotOrientedOpMode extends OpMode {

    // tuning numbers go here

    // max motor rpm
    private static final double MAX_RPM = 312;

    // intake servo speed
    private static final double INTAKE_SERVO_SPEED = 0.8;

    // servo direction, servo 2 gets set opposite. flip if they spin the
    // wrong way
    private static final DcMotorSimple.Direction INTAKE_SERVO_DIRECTION = DcMotorSimple.Direction.REVERSE;

    // camera servo moves this much per dpad press
    private static final double SERVO_STEP = 0.02;

    MecanumRobotDrive drive = new MecanumRobotDrive();

    // stick values
    double forward, strafe, rotate, intake;

    // camera servo pos 0 to 1
    private double cameraServoPos = 0.5;

    // intake on/off, A toggles it
    boolean intakeOn;

    @Override
    public void init() {

        drive.init(hardwareMap);

        // give the mechanism its numbers
        drive.setMaxRPM(MAX_RPM);
        drive.setIntakeServoSpeed(INTAKE_SERVO_SPEED);
        drive.setIntakeServoDirection(INTAKE_SERVO_DIRECTION);
        drive.setCameraServoPos(cameraServoPos);
    }

    @Override
    public void loop() {
        forward = -gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        rotate = gamepad1.right_stick_x;

        // A toggles intake (runs the servos too)
        if (gamepad1.aWasReleased()) {
            intakeOn = !intakeOn;
        }
        intake = intakeOn ? 1 : 0;

        // dpad moves the camera, clamp here so it cant wind past the ends
        if (gamepad1.dpadUpWasPressed()) {
            cameraServoPos = Math.min(1.0, cameraServoPos + SERVO_STEP);
        }
        if (gamepad1.dpadDownWasPressed()) {
            cameraServoPos = Math.max(0.0, cameraServoPos - SERVO_STEP);
        }
        drive.setCameraServoPos(cameraServoPos);

        drive.drive(forward, strafe, rotate, intake);

        telemetry.addData("Forward", forward);
        telemetry.addData("Strafe", strafe);
        telemetry.addData("Rotate", rotate);
        telemetry.addData("FrontLeft RPM", drive.frontLeftRPM());
        telemetry.addData("FrontRight RPM", drive.frontRightRPM());
        telemetry.addData("BackLeft RPM", drive.backLeftRPM());
        telemetry.addData("BackRight RPM", drive.backRightRPM());
        telemetry.addData("IntakeOn", intakeOn);
        telemetry.addData("CameraServoPos", drive.cameraServoPos());
        telemetry.addData("IntakeServo1Power", drive.intakeServo1Power());
        telemetry.addData("IntakeServo2Power", drive.intakeServo2Power());
        telemetry.update();
    }
}
