package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

// mecanum drive + intake + camera servo
public class MecanumRobotDrive {

    private DcMotorEx frontLeftMotor, backLeftMotor, frontRightMotor, backRightMotor, intakeMotor;
    private Servo CameraServo;
    private CRServo intakeServo1;
    private CRServo intakeServo2;
    private IMU imu;
    private DcMotor motor; // linear slide motor, not currently used
    private double ticksPerRevFL;
    private double ticksPerRevFR;
    private double ticksPerRevBL;
    private double ticksPerRevBR;

    // set from the opmode
    private double maxRPM;
    private double intakeServoSpeed;

    // grab all the hardware
    public void init(HardwareMap hwMap) {
     frontLeftMotor = hwMap.get(DcMotorEx.class, "front_left_motor");
     backLeftMotor = hwMap.get(DcMotorEx.class, "back_left_motor");
     frontRightMotor = hwMap.get(DcMotorEx.class, "front_right_motor");
     backRightMotor = hwMap.get(DcMotorEx.class, "back_right_motor");
     intakeMotor = hwMap.get(DcMotorEx.class, "intake_motor");
     CameraServo = hwMap.get(Servo.class, "camera_servo");
     intakeServo1 = hwMap.get(CRServo.class, "intake_servo_1");
     intakeServo2 = hwMap.get(CRServo.class, "intake_servo_2");

     // continuous servos spin forever if powered, stop them til drive runs
     intakeServo1.setPower(0.0);
     intakeServo2.setPower(0.0);

     backRightMotor.setDirection(DcMotor.Direction.REVERSE);
     intakeMotor.setDirection(DcMotor.Direction.REVERSE);

     frontLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
     backLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
     frontRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
     backRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

     imu = hwMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot RevOrientation = new RevHubOrientationOnRobot(
        RevHubOrientationOnRobot.LogoFacingDirection.FORWARD,
        RevHubOrientationOnRobot.UsbFacingDirection.UP);

        imu.initialize(new IMU.Parameters(RevOrientation));

        // ticks per rev for each wheel
        ticksPerRevFL = frontLeftMotor.getMotorType().getTicksPerRev();
        ticksPerRevFR = frontRightMotor.getMotorType().getTicksPerRev();
        ticksPerRevBL = backLeftMotor.getMotorType().getTicksPerRev();
        ticksPerRevBR = backRightMotor.getMotorType().getTicksPerRev();
    }
    // -1 to 1 for forward/strafe/rotate, intake is just 0 or 1
    public void drive(double forward, double strafe, double rotate, double intake) {
        // combine the inputs for each wheel
        double frontLeftPower = forward + strafe + rotate;
        double backLeftPower = forward - strafe + rotate;
        double frontRightPower = forward - strafe - rotate;
        double backRightPower = forward + strafe - rotate;

        // scale all wheels the same so the biggest one is full power
        double maxPower = 1.0;

        maxPower = Math.max(maxPower, Math.abs(frontLeftPower));
        maxPower = Math.max(maxPower, Math.abs(backLeftPower));
        maxPower = Math.max(maxPower, Math.abs(frontRightPower));
        maxPower = Math.max(maxPower, Math.abs(backRightPower));

        frontLeftPower = frontLeftPower / maxPower;
        backLeftPower = backLeftPower / maxPower;
        frontRightPower = frontRightPower / maxPower;
        backRightPower = backRightPower / maxPower;

        // power to rpm
        double frontLeftTargetRPM = frontLeftPower * maxRPM;
        double backLeftTargetRPM = backLeftPower * maxRPM;
        double frontRightTargetRPM = frontRightPower * maxRPM;
        double backRightTargetRPM = backRightPower * maxRPM;

        // rpm to ticks per sec
        double frontLeftVelocity = (frontLeftTargetRPM * ticksPerRevFL) / 60.0;
        double backLeftVelocity = (backLeftTargetRPM * ticksPerRevBL) / 60.0;
        double frontRightVelocity = (frontRightTargetRPM * ticksPerRevFR) / 60.0;
        double backRightVelocity = (backRightTargetRPM * ticksPerRevBR) / 60.0;

        // drive motors
        frontLeftMotor.setVelocity(frontLeftVelocity);
        backLeftMotor.setVelocity(backLeftVelocity);
        frontRightMotor.setVelocity(frontRightVelocity);
        backRightMotor.setVelocity(backRightVelocity);
        intakeMotor.setPower(intake);

        // intake servos follow the intake
        intakeServo1.setPower(intake * intakeServoSpeed);
        intakeServo2.setPower(intake * intakeServoSpeed);
    }

    // max motor speed
    public void setMaxRPM(double rpm) {
        maxRPM = rpm;
    }

    // intake servo speed
    public void setIntakeServoSpeed(double speed) {
        intakeServoSpeed = Math.max(-1.0, Math.min(1.0, speed));
    }

    // servo 2 goes opposite servo 1
    public void setIntakeServoDirection(DcMotorSimple.Direction direction) {
        intakeServo1.setDirection(direction);
        intakeServo2.setDirection(direction.inverted());
    }

    // keep in 0-1 or the sdk throws
    public void setCameraServoPos(double pos) {
        CameraServo.setPosition(Math.max(0.0, Math.min(1.0, pos)));
    }

    // camera servo position
    public double cameraServoPos() {
        return CameraServo.getPosition();
    }

    // servo 1 power
    public double intakeServo1Power() {
        return intakeServo1.getPower();
    }

    // servo 2 power
    public double intakeServo2Power() {
        return intakeServo2.getPower();
    }

    // field relative drive, no intake here
    public void driveFieldRelative(double forward, double strafe, double rotate) {
        double theta = Math.atan2(forward, strafe);
        double r = Math.hypot(strafe, forward);

        theta = AngleUnit.normalizeRadians(theta -
                imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));

        double newForward = r * Math.sin(theta);
        double newStrafe = r * Math.cos(theta);

        this.drive(newForward, newStrafe, rotate, 0);

    }
    // wheel revs since reset
    public double frontLeftMotor() {
        return frontLeftMotor.getCurrentPosition() / ticksPerRevFL;
    }

    public double frontRightMotor() {
        return frontRightMotor.getCurrentPosition() / ticksPerRevFR;
    }

    public double backLeftMotor() {
        return backLeftMotor.getCurrentPosition() / ticksPerRevBL;
    }

    public double backRightMotor() {
        return backRightMotor.getCurrentPosition() / ticksPerRevBR;
    }

    // wheel rpm
    public double frontLeftRPM() {
        return (frontLeftMotor.getVelocity() / ticksPerRevFL) * 60;
    }

    public double frontRightRPM() {
        return (frontRightMotor.getVelocity() / ticksPerRevFR) * 60;
    }

    public double backLeftRPM() {
        return (backLeftMotor.getVelocity() / ticksPerRevBL) * 60;
    }

    public double backRightRPM() {
        return (backRightMotor.getVelocity() / ticksPerRevBR) * 60;
    }
}
