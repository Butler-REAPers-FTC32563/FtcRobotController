package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class RumbleTest extends OpMode {

    @Override
    public void init() {

    }

    @Override
    public void loop() {
       if (gamepad1.a) {
           gamepad1.rumble(67);
       }
    }


}