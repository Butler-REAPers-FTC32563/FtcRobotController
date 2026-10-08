package org.firstinspires.ftc.teamcode.mechanisms;
import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;


import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import org.firstinspires.ftc.teamcode.pedro.Constants;

@Autonomous
public class ParkLeaveAuto extends OpMode {
    private Follower follower;
    private final PoseFactory poseFactory = PoseFactory.degrees();
    
    private final Pose startPose = poseFactory.of(60, 12, 90);
    private final Pose path1 = poseFactory.of(60, 36, 90);
    private final Pose point2 = poseFactory.of(18, 36, 180);
    private final Pose point3 = poseFactory.of(18, 108, 90);
    private final Pose point4 = poseFactory.of(9, 108, 180);

    private Path path1() {
        return Paths.line(startPose, path1).constant(path1);
    }
    private Path path2(){
        return Paths.line(path1, point2).constant(point2);
    }
    private Path path3() {
        return Paths.line(point2, point3).constant(point3);
    }
    private Path path4() {
        return Paths.line(point3, point4).constant(point4);
    }
    private Command autoRoutine() {
        return sequential(
                follow(follower, path1()),
                follow(follower, path2()),
                follow(follower, path3()),
                follow(follower, path4())
        );
    }
    @Override
    public void init() {
        Scheduler.reset();

        follower = Constants.create(hardwareMap);

        follower.setPose(startPose);
        follower.update();
    }



    @Override
    public void start() {
        schedule(autoRoutine());
    }
    @Override
    public void loop() {
        follower.update();
        Scheduler.execute();


    }
}
