package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.TankDrive;

@TeleOp
public class TankDriveOpMode extends OpMode {

    TankDrive drive = new TankDrive();
    double throttle, spin;

    @Override
    public void init()  {
    drive.init(hardwareMap);

    }

    @Override
    public void loop(){
    throttle = -gamepad1.left_stick_y;
    spin = gamepad1.right_stick_x;

    drive.drive(throttle, spin);

    }
}
