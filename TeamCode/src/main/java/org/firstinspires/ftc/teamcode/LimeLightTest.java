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

    private final double FORWARD_SPEED = 0.2; // default forward drive speed
    private final double ROTATE_SPEED = 0.025; //default rotation speed both ways

    TestBench bench = new TestBench();
    MecanumRobotDrive drive = new MecanumRobotDrive();
    double forward, strafe, rotate;
    public Limelight3A limelight3A;


    @Override
    public void init() {
        // select limelight and pipeline
        limelight3A = hardwareMap.get(Limelight3A.class, "limeLight");
        limelight3A.pipelineSwitch(8); //  pipeline 8 for green color detection

        drive.init(hardwareMap);


    }

    // start limelight
    @Override
    public void start() {
        limelight3A.start();

    }



    @Override
    public void loop() {

        // get latest limelight results
        LLResult llResult = limelight3A.getLatestResult();

        // if ball is found go towards it
        if (llResult !=null && llResult.isValid()) {

            

            // if x is not centered rotate counterclockwise
            if (llResult.getTx() >= 6) {
                forward = 0;
                rotate = ROTATE_SPEED;
            }
            // rotate counterclockwise
            else if (llResult.getTx() <= -6) {
                forward = 0;
                rotate = -ROTATE_SPEED;

            }
            // when x is centered drive towards the ball
            else if (Math.abs(llResult.getTx()) <= 6 && llResult.getTa() <= 15 ) {
                rotate = 0;
                forward = FORWARD_SPEED;
            }
            // if ball is detected but close enough stop motors
            else {
                rotate = 0;
                forward = 0;
                strafe = 0;
            }

            // if no ball found rotate clockwise to find ball
            if (!llResult.isValid()) {
                rotate = ROTATE_SPEED;
            }
            drive.drive(forward, strafe, rotate, 0);

            //update and add telemetry
            telemetry.update();
            telemetry.addData("Target X offset", llResult.getTx());
            telemetry.addData("Target Y offset", llResult.getTy());
            telemetry.addData("Target Area offset", llResult.getTa());
        }

    }

}

