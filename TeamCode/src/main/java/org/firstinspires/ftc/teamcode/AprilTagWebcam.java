package org.firstinspires.ftc.teamcode;

import android.util.Size;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.List;

public class AprilTagWebcam {

    private AprilTagProcessor aprilTagProcessor;

    private VisionPortal visionPortal;


    private List<AprilTagDetection> detectedTags = new ArrayList<>();

    private Telemetry telemetry;

    public void init(HardwareMap hwMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        aprilTagProcessor = new AprilTagProcessor.Builder()
            .setDrawTagID(false)
            .setDrawTagOutline(false)
            .setDrawAxes(false)
            .setDrawCubeProjection(false)
            .setOutputUnits(DistanceUnit.CM, AngleUnit.DEGREES)
            .setNumThreads(1)
            .build();

        VisionPortal.Builder builder = new VisionPortal.Builder();
        builder.setCamera(hwMap.get(WebcamName.class, "webcam 1"));
        builder.setCameraResolution(new Size(640, 480));
        builder.enableLiveView(false); // reduce latency

        builder.addProcessor(aprilTagProcessor);

        visionPortal = builder.build();
        // Important to wait for camera to be ready

        setManualExposure(6, 240);
    }

    private void setManualExposure(int exposureMs, int gain) {
        // Wait for the camera to be open and streaming.
        if (visionPortal == null || visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            telemetry.addData( "Camera", "Waiting ... ");
            // This loop will halt the init process until the camera is ready.
            while (visionPortal != null && visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
                try { Thread.sleep(20); } catch (InterruptedException ignored) {}
            }
            telemetry.addData( "Camera", "Ready");
            telemetry.update();
    }
        
}
}
