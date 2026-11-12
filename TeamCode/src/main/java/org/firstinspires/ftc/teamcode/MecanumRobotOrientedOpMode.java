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

    // latest AprilTag reading, refreshed every loop by addAprilTagTelemetry().
    // tagId stays -1 while no tag is in view, so tagRoll is only trustworthy
    // when tagId >= 0. When several tags share a frame, the first one wins.
    // This is the raw first-tag value; mechanismAngle below is the useful one.
    private int tagId = -1;
    private double tagRoll;

    // --- mechanism angle from its three AprilTags ---

    // IDs of the tags mounted on the mechanism. Leave this empty to accept any
    // tag in view, which is the only option while the IDs are still unknown.
    private static final int[] MECHANISM_TAG_IDS = {};

    // Mount angle of each tag in degrees, measured from the mechanism's zero
    // position, same sign convention as the roll reading. Index 0 belongs to
    // MECHANISM_TAG_IDS[0] and so on. Three tags on one flat face are all 0;
    // tags clocked around the rotation axis each need their own number.
    private static final double[] MECHANISM_TAG_MOUNT_DEGREES = {};

    // 0 to 1 exponential filter on the reported angle. 0 = raw/jittery,
    // 1 = frozen. 0.25 settles in a few frames at 50 Hz.
    private static final double MECHANISM_ANGLE_SMOOTHING = 0.25;

    // degrees subtracted so the mechanism's rest position reads 0. Y on
    // gamepad1 captures a new zero from the tag currently in view.
    private double mechanismZeroDegrees;

    // last mechanism angle, NaN until a tag has ever been seen
    private double mechanismAngle = Double.NaN;
    // which tag that angle came from, -1 when the last frame had none
    private int mechanismTagId = -1;
    // roll + mount offset before zeroing/smoothing, needed to capture a zero
    private double mechanismRawDegrees = Double.NaN;

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
        // Stick layout: left stick = forward/back + rotate, right stick left/right = strafe.
        // This is paired with STRAFE_SIGN/ROTATE_SIGN in MecanumRobotDrive: this robot's drive
        // is mirrored left<->right, so those two signs are what make these sticks drive straight.
        // Changing one without the other puts strafe and rotate back on the wrong axes.
        forward = -gamepad1.left_stick_y;
        strafe = gamepad1.right_stick_x;
        rotate = gamepad1.left_stick_x;

        // A toggles intake (runs the servos too)
        if (gamepad1.aWasReleased()) {
            intakeOn = !intakeOn;
        }
        intake = intakeOn ? 1 : 0;

        // Y re-zeroes the mechanism angle at wherever the mechanism sits now
        if (gamepad1.yWasPressed()) {
            rezeroMechanism();
        }

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
        telemetry.addData("TagVar", tagRoll);

        addAprilTagTelemetry();

        telemetry.update();
    }

    // reads the april tags out of the limelight's fiducial pipeline
    private void addAprilTagTelemetry() {
        // drop the previous reading first: a loop with no tag must not leave a
        // stale angle behind for the drive code to use
        tagId = -1;
        tagRoll = 0;

        LLResult result = limelight3A.getLatestResult();
        if (result == null || !result.isValid()) {
            mechanismTagId = -1;
            telemetry.addData("Limelight", "no valid result");
            return;
        }

        for (LLResultTypes.FiducialResult tag : result.getFiducialResults()) {

            // 3d pose roll, needs the tag pose to be computed by the pipeline
            double poseRoll = tag.getTargetPoseCameraSpace()
                    .getOrientation()
                    .getRoll(AngleUnit.DEGREES);

            // keep the first tag of the frame as the opmode-wide angle
            if (tagId < 0) {
                tagId = tag.getFiducialId();
                tagRoll = poseRoll;
            }

            //Telemetry for the roll of the april tag
            String prefix = "Tag " + tag.getFiducialId() + " ";
            telemetry.addData(prefix + "Roll(pose)", "%.1f deg", poseRoll);
            telemetry.addData(prefix + "TX/TY", "%.1f / %.1f deg",
                    tag.getTargetXDegrees(), tag.getTargetYDegrees());
        }

        updateMechanismAngle(result);

        if (result.getFiducialResults().isEmpty()) {
            telemetry.addData("Limelight", "no tags in view");
        } else {
            telemetry.addData("TagRoll", "%.1f deg (id %d)", tagRoll, tagId);
        }

        if (Double.isNaN(mechanismAngle)) {
            telemetry.addData("MechanismAngle", "no mechanism tag seen yet");
        } else {
            telemetry.addData("MechanismAngle", "%.1f deg (tag %d)", mechanismAngle, mechanismTagId);
        }
    }

    // Picks the best visible mechanism tag and turns its roll into an angle.
    // "Best" is the largest target area: a tag seen big and square is the most
    // frontal one, so its pose-derived roll carries the least perspective error.
    // Coplanar tags need no averaging for this to be right, and a tag clocked
    // further round the mechanism carries its own mount offset instead.
    private void updateMechanismAngle(LLResult result) {
        LLResultTypes.FiducialResult best = null;
        for (LLResultTypes.FiducialResult tag : result.getFiducialResults()) {
            if (!isMechanismTag(tag.getFiducialId())) {
                continue;
            }
            if (best == null || tag.getTargetArea() > best.getTargetArea()) {
                best = tag;
            }
        }

        if (best == null) {
            // no mechanism tag this frame: keep the last angle and filter state,
            // a mechanism does not move because the camera lost sight of it
            mechanismTagId = -1;
            return;
        }

        mechanismTagId = best.getFiducialId();
        mechanismRawDegrees = best.getTargetPoseCameraSpace()
                .getOrientation()
                .getRoll(AngleUnit.DEGREES)
                + mountOffsetFor(mechanismTagId);

        double target = wrapDegrees(mechanismRawDegrees - mechanismZeroDegrees);
        mechanismAngle = Double.isNaN(mechanismAngle)
                ? target  // first reading after start: no history to filter against
                : wrapDegrees(mechanismAngle
                        + MECHANISM_ANGLE_SMOOTHING * wrapDegrees(target - mechanismAngle));
    }

    // stores the current reading as the new zero, so the mechanism reads 0 here
    private void rezeroMechanism() {
        if (Double.isNaN(mechanismRawDegrees)) {
            return;  // nothing seen yet, nothing to zero against
        }
        mechanismZeroDegrees = mechanismRawDegrees;
        mechanismAngle = 0;
    }

    private boolean isMechanismTag(int fiducialId) {
        // unconfigured: any tag in view is treated as a mechanism tag
        if (MECHANISM_TAG_IDS.length == 0) {
            return true;
        }
        for (int id : MECHANISM_TAG_IDS) {
            if (id == fiducialId) {
                return true;
            }
        }
        return false;
    }

    private static double mountOffsetFor(int fiducialId) {
        for (int i = 0; i < MECHANISM_TAG_IDS.length; i++) {
            if (MECHANISM_TAG_IDS[i] == fiducialId && i < MECHANISM_TAG_MOUNT_DEGREES.length) {
                return MECHANISM_TAG_MOUNT_DEGREES[i];
            }
        }
        return 0;
    }

    // keeps an angle in -180..180 so a wrap cannot lag the filter by a full turn
    private static double wrapDegrees(double degrees) {
        double wrapped = degrees % 360.0;
        if (wrapped > 180.0) {
            wrapped -= 360.0;
        } else if (wrapped <= -180.0) {
            wrapped += 360.0;
        }
        if (wrapped == 0.0) {
            wrapped = 0.0;  // turns -0.0 into 0.0 so telemetry never prints "-0.0"
        }
        return wrapped;
    }
}
