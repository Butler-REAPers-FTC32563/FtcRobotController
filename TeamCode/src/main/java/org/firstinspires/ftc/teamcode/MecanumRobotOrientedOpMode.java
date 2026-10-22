package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.mechanisms.MecanumRobotDrive;

@TeleOp
public class MecanumRobotOrientedOpMode extends OpMode {

    // tuning numbers go here

    // max motor rpm
    private static final double MAX_RPM = 312;

    // servo direction, right gets set opposite left. flip if they spin the
    // wrong way
    private static final DcMotorSimple.Direction INTAKE_SERVO_DIRECTION = DcMotorSimple.Direction.REVERSE;

    // camera servo moves this much per dpad press
    private static final double SERVO_STEP = 0.04;

    MecanumRobotDrive drive = new MecanumRobotDrive();

    // stick values
    double forward, strafe, rotate, intake;

    // camera servo pos 0 to 1
    private double cameraServoPos = 0.5;

    // intake on/off, A toggles it
    boolean intakeOn;

    public Limelight3A limelight3A;

    @Override
    public void init() {
        // setup limelight and pipeline
        limelight3A = hardwareMap.get(Limelight3A.class, "limeLight");
        limelight3A.pipelineSwitch(2); //  pipeline 8 for Apriltags

        drive.init(hardwareMap);

        // give the mechanism its numbers
        drive.setMaxRPM(MAX_RPM);
        drive.setIntakeServoDirection(INTAKE_SERVO_DIRECTION);
        drive.setCameraServoPos(cameraServoPos);


    }

    @Override
    public void start() {
        // the limelight only returns results once it is running
        limelight3A.start();
    }

    @Override
    public void loop() {
        forward = -gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        rotate = gamepad1.right_stick_x;

        // A toggles intake (runs the servos too)
        if (gamepad1.aWasReleased()) {
            intakeOn = !intakeOn;
        }
        intake = intakeOn ? 1 : 0;

        // dpad moves the camera, clamp here so it cant wind past the ends
        if (gamepad1.dpadUpWasPressed()) {
            cameraServoPos = Math.min(1.0, cameraServoPos + SERVO_STEP);
        }
        if (gamepad1.dpadDownWasPressed()) {
            cameraServoPos = Math.max(0.0, cameraServoPos - SERVO_STEP);
        }
        drive.setCameraServoPos(cameraServoPos);

        drive.drive(forward, strafe, rotate, intake);

        telemetry.addData("Forward", forward);
        telemetry.addData("Strafe", strafe);
        telemetry.addData("Rotate", rotate);
        telemetry.addData("FrontLeft RPM", drive.frontLeftRPM());
        telemetry.addData("FrontRight RPM", drive.frontRightRPM());
        telemetry.addData("BackLeft RPM", drive.backLeftRPM());
        telemetry.addData("BackRight RPM", drive.backRightRPM());
        telemetry.addData("IntakeOn", intakeOn);
        telemetry.addData("CameraServoPos", drive.cameraServoPos());
        telemetry.addData("IntakeServoLeftPower", drive.intakeServoLeftPower());
        telemetry.addData("IntakeServoRightPower", drive.intakeServoRightPower());

        addAprilTagTelemetry();

        telemetry.update();
    }

    // reads the april tags out of the limelight's fiducial pipeline
    private void addAprilTagTelemetry() {
        LLResult result = limelight3A.getLatestResult();
        if (result == null || !result.isValid()) {
            telemetry.addData("Limelight", "no valid result");
            return;
        }

        for (LLResultTypes.FiducialResult tag : result.getFiducialResults()) {

            // 3d pose roll, needs the tag pose to be computed by the pipeline
            double poseRoll = tag.getTargetPoseCameraSpace()
                    .getOrientation()
                    .getRoll(AngleUnit.DEGREES);

            //Telemetry for the roll of the april tag
            String prefix = "Tag " + tag.getFiducialId() + " ";
            telemetry.addData(prefix + "Roll(pose)", "%.1f deg", poseRoll);
            telemetry.addData(prefix + "TX/TY", "%.1f / %.1f deg",
                    tag.getTargetXDegrees(), tag.getTargetYDegrees());
        }

        if (result.getFiducialResults().isEmpty()) {
            telemetry.addData("Limelight", "no tags in view");
        }
    }
}
