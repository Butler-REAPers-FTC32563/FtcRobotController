//TODO
// Add a deadzone since controls are very sensitve

package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class TankDrive {
   private DcMotor backLeftMotor, backRightMotor, frontLeftMotor, frontRightMotor;

   public void init(HardwareMap hwMap) {
       frontLeftMotor = hwMap.get(DcMotor.class, "front_left_motor");
       frontRightMotor = hwMap.get(DcMotor.class, "front_right_motor");
       backLeftMotor = hwMap.get(DcMotor.class, "back_left_motor");
       backRightMotor = hwMap.get(DcMotor.class, "back_right_motor");

       frontLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
       frontRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
       backLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
       backRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

       backRightMotor.setDirection(DcMotor.Direction.REVERSE);
       frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
   }

   public void drive(double throttle, double spin) {
       double leftPower = throttle + spin;
       double rightPower = throttle - spin;
       double largest = Math.max(Math.abs(leftPower), Math.abs(rightPower));
       if (largest > 1.0) {
           leftPower /= largest;
           rightPower /= largest;
       }

       frontLeftMotor.setPower(leftPower);
       frontRightMotor.setPower(rightPower);
      backLeftMotor.setPower(leftPower);
       backRightMotor.setPower(rightPower);


   }




}
