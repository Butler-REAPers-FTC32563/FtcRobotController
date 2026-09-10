package org.firstinspires.ftc.teamcode.mechanisms;
import static java.lang.Math.abs;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class MecanumRobotDrive {
    private DcMotorEx frontLeftMotor, backLeftMotor, frontRightMotor, backRightMotor, intakeMotor, ArmExtendMotor, ArmAngleMotor;
    private IMU imu;
    private DcMotor motor; //linear slide motor 0
    private double ticksPerRevFL;
    private double ticksPerRevFR;
    private double ticksPerRevBL;
    private double ticksPerRevBR;
    private Servo ClawServo;
    private CRServo ConServo;

    public void init(HardwareMap hwMap) {
     frontLeftMotor = hwMap.get(DcMotorEx.class, "front_left_motor");
     backLeftMotor = hwMap.get(DcMotorEx.class, "back_left_motor");
     frontRightMotor = hwMap.get(DcMotorEx.class, "front_right_motor");
     backRightMotor = hwMap.get(DcMotorEx.class, "back_right_motor");
     intakeMotor = hwMap.get(DcMotorEx.class, "intake_motor");
     ArmExtendMotor = hwMap.get(DcMotorEx.class, "Arm_Extend_Motor");
     ArmAngleMotor = hwMap.get(DcMotorEx.class, "Arm_Extend_Motor");
     ClawServo = hwMap.get(Servo.class, "Claw_Servo");
     ConServo = hwMap.get(CRServo.class, "ConServo");

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

        // Dc motor
        ticksPerRevFL = frontLeftMotor.getMotorType().getTicksPerRev();
        ticksPerRevFR = frontRightMotor.getMotorType().getTicksPerRev();
        ticksPerRevBL = backLeftMotor.getMotorType().getTicksPerRev();
        ticksPerRevBR = backRightMotor.getMotorType().getTicksPerRev();


    }

    public void setConServo(double power) {
        ConServo.setPower(power);
    }

    public void setClawPos(double position) {
        ClawServo.setPosition(position);
    }

    public void drive(double forward, double strafe, double rotate, double intake) {
        double frontLeftPower = forward + strafe + rotate;
        double backLeftPower = forward - strafe + rotate;
        double frontRightPower = forward - strafe - rotate;
        double backRightPower = forward + strafe - rotate;

        double maxPower = 1.0;

        maxPower = Math.max(maxPower, Math.abs(frontLeftPower));
        maxPower = Math.max(maxPower, Math.abs(backLeftPower));
        maxPower = Math.max(maxPower, Math.abs(frontRightPower));
        maxPower = Math.max(maxPower, Math.abs(backRightPower));

        // Normalize power values
        frontLeftPower = frontLeftPower / maxPower;
        backLeftPower = backLeftPower / maxPower;
        frontRightPower = frontRightPower / maxPower;
        backRightPower = backRightPower / maxPower;

        //normalize power to target RPM 
        double maxRPM = 312;
        double frontLeftTargetRPM = frontLeftPower * maxRPM;
        double backLeftTargetRPM = backLeftPower * maxRPM;
        double frontRightTargetRPM = frontRightPower * maxRPM;
        double backRightTargetRPM = backRightPower * maxRPM;

        // Convert RPM to tps with velocity
        double frontLeftVelocity = (frontLeftTargetRPM * ticksPerRevFL) / 60.0;
        double backLeftVelocity = (backLeftTargetRPM * ticksPerRevBL) / 60.0;
        double frontRightVelocity = (frontRightTargetRPM * ticksPerRevFR) / 60.0;
        double backRightVelocity = (backRightTargetRPM * ticksPerRevBR) / 60.0;

        // Set velocity 
        frontLeftMotor.setVelocity(frontLeftVelocity);
        backLeftMotor.setVelocity(backLeftVelocity);
        frontRightMotor.setVelocity(frontRightVelocity);
        backRightMotor.setVelocity(backRightVelocity);
        intakeMotor.setPower(intake);

    }

    public void driveFieldRelative(double forward, double strafe, double rotate) {
        double theta = Math.atan2(forward, strafe);
        double r = Math.hypot(strafe, forward);

        theta = AngleUnit.normalizeRadians(theta -
                imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));

        double newForward = r * Math.sin(theta);
        double newStrafe = r * Math.cos(theta);

        this.drive(newForward, newStrafe, rotate, 0);

    }
    // normalizing ticks to revolutions
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

    // RPM calculations
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