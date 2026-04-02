package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class UseRobotLocationOpMode extends OpMode {
    RobotLocationPractice robotLocationPractice = new RobotLocationPractice(0);


    @Override
    public void init() {
        robotLocationPractice.setAngle(0);
    }

    @Override
    public void loop() {
        if (gamepad1.a) {
            robotLocationPractice.turnRobot(0.1);
        }
        else if (gamepad1.b) {
            robotLocationPractice.turnRobot(-0.1);

        }
        telemetry.addData("getAngle", robotLocationPractice.getAngle;

        telemetry.addData("Heading", robotLocationPractice.getHeading());
    }
}
