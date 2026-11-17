package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.mechanisms.MecanumRobotDrive;

import java.util.Collections;
import java.util.List;


//todo make a timer work in auto as an if statement so if auto time is ending go park
@TeleOp
public class MecanumRobotOrientedOpMode extends OpMode {

    // tuning numbers go here

    // motor free speed in rpm
    private static final double MAX_RPM = 312;

    // intake servo direction, right gets set opposite left
    private static final DcMotorSimple.Direction INTAKE_SERVO_DIRECTION = DcMotorSimple.Direction.REVERSE;

    // camera servo change per dpad press
    private static final double SERVO_STEP = 0.04;

    // a cell is scorable while its tags are this close to upright
    private static final double CELL_UP_DEGREES = 90;
    private static final double CELL_UP_HYSTERESIS = 5;

    // roll a hive tag reads when its cell is right side up. the tags on the
    // hive are not all mounted the same way, so measure it on the field: put a
    // cell you know is up in front of the camera, read HiveRoll in telemetry,
    // and put that number here. 0 = tags read 0 when up, which may be wrong.
    private static final double HIVE_ROLL_WHEN_UP = 0;

    // goBILDA light colours as servo positions, off the goBILDA chart
    private static final double LIGHT_OFF_POS = 0.0;
    private static final double LIGHT_RED_POS = 0.279;
    private static final double LIGHT_GREEN_POS = 0.5;

    MecanumRobotDrive drive = new MecanumRobotDrive();

    // joystick and intake inputs
    double forward, strafe, rotate, intake;

    // camera servo pos 0 to 1
    private double cameraServoPos = 0.5;

    // intake on/off, toggled with A
    boolean intakeOn;

    // whether the cell we are looking at can be scored, unknown until a hive
    // tag is in view
    private HiveCellState cellState = HiveCellState.UNKNOWN;
    private int framesWithoutHiveTag = 0;

    // first tag in this frame's reading, set by updateAprilTagState().
    // tagId is -1 when no tag is in view.
    private int tagId = -1;
    private double tagRoll;

    // Limelight result this frame's tags came from, reused by telemetry
    private LLResult tagResult;
    private List<LLResultTypes.FiducialResult> tagFiducials = Collections.emptyList();

    // hive tags, 4 per cell: 30-33 red scoring, 34-37 red audience,
    // 38-41 blue audience, 42-45 blue scoring
    private static final int[] HIVE_TAG_IDS = {
            30, 31, 32, 33,
            34, 35, 36, 37,
            38, 39, 40, 41,
            42, 43, 44, 45,
    };

    // angle smoothing 0 to 1. 0 = raw, 1 = frozen.
    private static final double HIVE_ANGLE_SMOOTHING = 0.25;

    // degrees subtracted so a chosen position reads 0. Y sets a new zero.
    private double hiveZeroDegrees;

    // last hive angle, NaN until a tag has been seen
    private double hiveAngle = Double.NaN;
    // roll of the tag the angle came from, before zeroing. this is what says
    // whether the cell is up.
    private double hiveRoll = Double.NaN;
    // tag the angle came from, -1 if the last frame had none
    private int hiveTagId = -1;

    public Limelight3A limelight3A;

    @Override
    public void init() {
        // setup limelight and pipeline
        limelight3A = hardwareMap.get(Limelight3A.class, "limeLight");
        limelight3A.pipelineSwitch(2); // AprilTag pipeline, check the number in the Limelight UI

        drive.init(hardwareMap);

        drive.setMaxRPM(MAX_RPM);
        drive.setIntakeServoDirection(INTAKE_SERVO_DIRECTION);
        drive.setCameraServoPos(cameraServoPos);
    }

    @Override
    public void start() {
        // Limelight only returns results after start()
        limelight3A.start();
    }

    @Override
    public void stop() {
        // The SDK already zeroes motors on a Driver Station STOP. This is about
        // releasing the Limelight's polling thread and leaving the light off for
        // the next run — start() recreates the poller when the opmode is reused.
        if (limelight3A != null) {
            limelight3A.stop();
        }
        drive.stopAll();
    }

    @Override
    public void loop() {
        // read the tags first so the rest of loop() uses this frame's values
        updateAprilTagState();

        // standard stick layout: left = forward/back + strafe, right = rotate
        forward = deadzone(-gamepad1.left_stick_y);
        strafe = deadzone(gamepad1.left_stick_x);
        rotate = deadzone(gamepad1.right_stick_x);

        // A toggles the intake, servos follow
        if (gamepad1.aWasReleased()) {
            intakeOn = !intakeOn;
        }
        intake = intakeOn ? 1 : 0;

        // Y sets the current position as the new zero
        if (gamepad1.yWasPressed()) {
            rezeroHive();
        }

        // dpad moves the camera servo, kept in 0 to 1
        if (gamepad1.dpadUpWasPressed()) {
            cameraServoPos = Math.min(1.0, cameraServoPos + SERVO_STEP);
        }
        if (gamepad1.dpadDownWasPressed()) {
            cameraServoPos = Math.max(0.0, cameraServoPos - SERVO_STEP);
        }

        // light: red while the cell is up, green while it is down, off when we
        // cannot tell
        updateCellState();
        drive.setTestLightPos(lightPosFor(cellState));

        drive.setCameraServoPos(cameraServoPos);

        drive.drive(forward, strafe, rotate);
        drive.setIntake(intake);

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
        telemetry.addData("Hive", cellState);

        addAprilTagTelemetry();

        telemetry.update();
    }

    // reads the tags, updates tagId, tagRoll and the hive angle
    private void updateAprilTagState() {
        // clear the old reading first so nothing stale survives this loop
        tagId = -1;
        tagRoll = 0;

        tagResult = limelight3A.getLatestResult();
        if (tagResult == null || !tagResult.isValid()) {
            tagResult = null;  // nothing valid this frame, telemetry says so below
            tagFiducials = Collections.emptyList();
            hiveTagId = -1;
            return;
        }

        tagFiducials = tagResult.getFiducialResults();

        for (LLResultTypes.FiducialResult tag : tagFiducials) {
            // first tag in the frame wins
            if (tagId < 0) {
                tagId = tag.getFiducialId();
                // roll from the tag's 3D pose (pipeline has to output pose)
                tagRoll = tag.getTargetPoseCameraSpace()
                        .getOrientation()
                        .getRoll(AngleUnit.DEGREES);
            }
        }

        updateHiveAngle();
    }

    // prints the tags this frame read
    private void addAprilTagTelemetry() {
        if (tagResult == null) {
            telemetry.addData("Limelight", "no valid result");
        } else if (tagFiducials.isEmpty()) {
            telemetry.addData("Limelight", "no tags in view");
        } else {
            for (LLResultTypes.FiducialResult tag : tagFiducials) {
                // roll of each visible tag
                String prefix = "Tag " + tag.getFiducialId() + " ";
                telemetry.addData(prefix + "Roll(pose)", "%.1f deg",
                        tag.getTargetPoseCameraSpace()
                                .getOrientation()
                                .getRoll(AngleUnit.DEGREES));
                telemetry.addData(prefix + "TX/TY", "%.1f / %.1f deg",
                        tag.getTargetXDegrees(), tag.getTargetYDegrees());
            }
            telemetry.addData("TagRoll", "%.1f deg (id %d)", tagRoll, tagId);
        }

        if (Double.isNaN(hiveAngle)) {
            telemetry.addData("HiveAngle", "no hive tag seen yet");
        } else {
            telemetry.addData("HiveAngle", "%.1f deg (tag %d)", hiveAngle, hiveTagId);
        }
        if (Double.isNaN(hiveRoll)) {
            telemetry.addData("HiveRoll", "no hive tag seen yet");
        } else {
            telemetry.addData("HiveRoll", "%.1f deg (tag %d)", hiveRoll, hiveTagId);
        }
    }

    // biggest hive tag in view, its roll becomes the hive angle
    private void updateHiveAngle() {
        LLResultTypes.FiducialResult best = null;
        for (LLResultTypes.FiducialResult tag : tagFiducials) {
            if (!isHiveTag(tag.getFiducialId())) {
                continue;
            }
            if (best == null || tag.getTargetArea() > best.getTargetArea()) {
                best = tag;
            }
        }

        if (best == null) {
            // no tag this frame: keep the last angle
            hiveTagId = -1;
            return;
        }

        hiveTagId = best.getFiducialId();
        hiveRoll = best.getTargetPoseCameraSpace()
                .getOrientation()
                .getRoll(AngleUnit.DEGREES);

        double target = wrapDegrees(hiveRoll - hiveZeroDegrees);
        hiveAngle = Double.isNaN(hiveAngle)
                ? target  // first reading after start: no history to filter against
                : wrapDegrees(hiveAngle
                        + HIVE_ANGLE_SMOOTHING * wrapDegrees(target - hiveAngle));
    }

    // current reading becomes the new zero
    private void rezeroHive() {
        if (hiveTagId < 0) {
            return;  // no hive tag in view, don't zero against a stale roll
        }
        hiveZeroDegrees = hiveRoll;
        hiveAngle = 0;
    }

    // up, down, or unknown while there is no hive tag to look at
    private enum HiveCellState {
        UP,
        DOWN,
        UNKNOWN
    }

    // red = cell up, green = cell down, off = we do not know
    private static double lightPosFor(HiveCellState state) {
        switch (state) {
            case UP:
                return LIGHT_RED_POS;
            case DOWN:
                return LIGHT_GREEN_POS;
            default:
                return LIGHT_OFF_POS;
        }
    }

    private void updateCellState() {
        if (hiveTagId < 0) {
            // hold the last reading briefly so one bad frame does not blink
            // the light off and back on
            if (++framesWithoutHiveTag < 5) {
                return;
            }
            cellState = HiveCellState.UNKNOWN;
            return;
        }
        framesWithoutHiveTag = 0;
        double roll = Math.abs(wrapDegrees(hiveRoll - HIVE_ROLL_WHEN_UP));
        if (roll < CELL_UP_DEGREES - CELL_UP_HYSTERESIS) {
            cellState = HiveCellState.UP;
        } else if (roll > CELL_UP_DEGREES + CELL_UP_HYSTERESIS) {
            cellState = HiveCellState.DOWN;
        }
    }

    private boolean isHiveTag(int fiducialId) {
        for (int id : HIVE_TAG_IDS) {
            if (id == fiducialId) {
                return true;
            }
        }
        return false;
    }

    // ignore stick noise at center and smooth the rest back to full range
    private static double deadzone(double value) {
        double magnitude = Math.abs(value) - 0.05;
        if (magnitude <= 0) {
            return 0;
        }
        return Math.copySign(magnitude / 0.95, value);
    }

    private static double wrapDegrees(double degrees) {
        double wrapped = degrees % 360.0;
        if (wrapped > 180.0) {
            wrapped -= 360.0;
        } else if (wrapped <= -180.0) {
            wrapped += 360.0;
        }
        return wrapped;
    }
}
