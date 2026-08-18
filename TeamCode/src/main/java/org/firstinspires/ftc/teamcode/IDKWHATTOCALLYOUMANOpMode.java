package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;





import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.TankDrive;

@TeleOp
public class IDKWHATTOCALLYOUMANOpMode extends OpMode {

    TankDrive drive = new TankDrive();
    double throttleL, throttleR, throttle;

    @Override
    public void init()  {
        drive.init(hardwareMap);

    }

    @Override
    public void loop(){
        throttleL = -gamepad1.left_stick_y * throttle;
        throttleR = gamepad1.right_stick_x * throttle;

        drive.drive( throttleL, throttleR);

    }
}
