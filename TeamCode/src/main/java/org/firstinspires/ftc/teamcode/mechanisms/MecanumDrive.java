package org.firstinspires.ftc.teamcode.mechanisms;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.IMU;

public class MecanumDrive {
    private DcMotor frontLeftMotor, backLeftMotor, frontRightMotor, backRightMotor;
    private IMU imu;

    public void init(HardwareMap hwMap) {
     frontLeftMotor = hwMap.get(DcMotor.class, "front_left_motor");
     backLeftMotor = hwMap.get(DcMotor.class, "back_left_motor");
     frontRightMotor = hwMap.get(DcMotor.class, "front_right_motor");
     backRightMotor = hwMap.get(DcMotor.class, "back_right_motor");

     frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
     backLeftMotor.setDirection(DcMotor.Direction.REVERSE);

     frontLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
     backLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
     frontRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
     backRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

     imu = hwMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot RevOrientation = new RevHubOrientationOnRobot(
        RevHubOrientationOnRobot.LogoFacingDirection.UP,
        RevHubOrientationOnRobot.UsbFacingDirection.FORWARD);
    }

}
