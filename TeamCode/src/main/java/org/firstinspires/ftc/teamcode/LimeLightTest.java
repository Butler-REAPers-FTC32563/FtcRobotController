package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.MecanumRobotDrive;
import org.firstinspires.ftc.teamcode.mechanisms.TestBench;

@Autonomous
public class LimeLightTest extends OpMode {

    TestBench bench = new TestBench();

    MecanumRobotDrive drive = new MecanumRobotDrive();

    double forward, strafe, rotate;

    public Limelight3A limelight3A;

    @Override
    public void init() {
        limelight3A = hardwareMap.get(Limelight3A.class, "limeLight");
        limelight3A.pipelineSwitch(8); //  pipeline 8 for green ball

        drive.init(hardwareMap);
    }

    @Override
    public void start() {

        limelight3A.start();
    }



    @Override
    public void loop() {
        LLResult llResult = limelight3A.getLatestResult();
        if (llResult !=null & llResult.isValid()) { // if results are valid

            if (Math.abs(llResult.getTx()) >= 10) { // if x is not centered rotate
                forward = 0;
                rotate = 0.5;
            } else if (Math.abs(llResult.getTx()) >= 10 & llResult.getTa() <= 80 ) {
                rotate = 0;
                forward = .5;
            } else {
                rotate = 0;
                forward = 0;
                strafe = 0;
            }


            drive.drive(forward, strafe, rotate);

            

            telemetry.addData("Target X offset", llResult.getTx());
            telemetry.addData("Target Y offset", llResult.getTy());
            telemetry.addData("Target Area offset", llResult.getTa());
        }

    }

}

