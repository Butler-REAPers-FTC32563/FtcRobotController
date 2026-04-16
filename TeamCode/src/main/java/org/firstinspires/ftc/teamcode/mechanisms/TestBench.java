package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class TestBench {
    private DigitalChannel touchSensor;
    private DcMotor motor; //linear slide motor 0
    private double ticksPerRev; //revolution

    public DcMotorSimple.Direction zeroPowerBehaviorSet;


    public void init(HardwareMap hwMap) {
       // Touch Sensor
        touchSensor = hwMap.get(DigitalChannel.class, "touch_sensor");
        touchSensor.setMode(DigitalChannel.Mode.INPUT);

        // DC motor
        motor = hwMap.get(DcMotor.class, "motor");
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        ticksPerRev = motor.getMotorType().getTicksPerRev();
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);



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




}


