package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class TestBench {
    private DigitalChannel touchSensor;
    private DcMotor motor; //linear slide motor 0
    private double ticksPerRev; //revolution
    private IMU imu;
    private Servo servoPos;
    private CRServo servoRot;
    NormalizedColorSensor colorSensor;
    public DcMotorSimple.Direction zeroPowerBehaviorSet;

    public enum DetectedColor {
        RED,
        BLUE,
        YELLOW,
        UNKNOWN

    }

    public void init(HardwareMap hwMap) {
       // Touch Sensor
        touchSensor = hwMap.get(DigitalChannel.class, "touch_sensor");
        touchSensor.setMode(DigitalChannel.Mode.INPUT);

        // DC motor
        motor = hwMap.get(DcMotor.class, "motor");
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        ticksPerRev = motor.getMotorType().getTicksPerRev();
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //IMU
        imu = hwMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot RevOrientation = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
        );

        imu.initialize(new IMU.Parameters(RevOrientation));

        //Servo
        servoPos = hwMap.get(Servo.class, "servo_pos");
        servoRot = hwMap.get(CRServo.class, "servo_rot");
        servoPos.scaleRange(0.5,1.0);
        servoPos.setDirection(Servo.Direction.REVERSE);
        servoRot.setDirection(CRServo.Direction.REVERSE);

        //Color Sensor
        colorSensor = hwMap.get(NormalizedColorSensor.class, "sensor_color"); // config name might be incorrect
        colorSensor.setGain(8); //set gain of sensor can turn for accuracy
    }

    public double getHeading(AngleUnit angleUnit) {
        return imu.getRobotYawPitchRollAngles().getYaw(angleUnit);
    }
// Touch Sensor
        public boolean isTouchSensorPressed() {
            return !touchSensor.getState();
        }

        public boolean isTouchSensorReleased() {
            return touchSensor.getState();
        }


// Dc Motor
    public void setMotorSpeed(double speed) {
        //values from -1.0 - 1.0
        motor.setPower(speed);

    }

    public double getMotorRevs() {
        return motor.getCurrentPosition() / ticksPerRev; // normalizing ticks to revolutions
    }

    public void setMotorBehavior(DcMotor.ZeroPowerBehavior zeroBehaviors) {
        motor.setZeroPowerBehavior(zeroBehaviors);
    }

// Servo
    public void setServoPos(double angle) {
    servoPos.setPosition(angle);
}

    public void setServoRot(double power) {
        servoRot.setPower(power);
    }

    // Color Sensor
    public TestBench.DetectedColor getDetectedColor(Telemetry telemetry) {
        NormalizedRGBA colors = colorSensor.getNormalizedColors(); // returns 4 values

        float normRed, normGreen, normBlue;
        normRed = colors.red / colors.alpha;
        normGreen = colors.green / colors.alpha;
        normBlue = colors.blue / colors.alpha;

        telemetry.addData("Red", normRed);
        telemetry.addData("Green", normGreen);
        telemetry.addData("Blue", normBlue);

        // TODO Change colors for our needs

        /*
        red, green, blue
        RED = >.35, <.3, <.3
        Yellow = >.5, >.9 < .6
        BLUE = <.2, <.5, >.5
         */

        if (normRed > 0.35 && normGreen < 0.3 && normBlue < 0.3) {
            return TestBench.DetectedColor.RED;
        }
        else if (normRed > 0.5 && normGreen > 0.9 && normBlue < 0.6) {
            return TestBench.DetectedColor.YELLOW;
        }
        else if (normRed < 0.2 && normGreen < 0.5 && normBlue > 0.5) {
            return TestBench.DetectedColor.BLUE;

        }

        return TestBench.DetectedColor.UNKNOWN;


    }
}


