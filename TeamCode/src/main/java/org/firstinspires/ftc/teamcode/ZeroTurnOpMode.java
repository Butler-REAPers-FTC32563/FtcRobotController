package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class ZeroTurnOpMode extends OpMode {

    ZeroTurn drive = new ZeroTurn();
    double throttleL, throttleR, throttle;

    @Override
    public void init()  {
        drive.init(hardwareMap);

    }

    @Override
    public void loop(){
        throttleL = -gamepad1.left_stick_y * throttle;
        throttleR = gamepad1.right_stick_x * throttle;

        drive.drive(throttleL, throttleR);

    }
}
