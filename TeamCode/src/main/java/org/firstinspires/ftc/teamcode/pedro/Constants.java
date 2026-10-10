package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
    public static Follower create(HardwareMap h) {
        return new Follower(
                new PinpointLocalizer(h,localizerConfig),
                new Mecanum(h, drivetrainConfig),
                new Foresight(foresightConfig)
        );
    }

    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
        c.frontLeftName.set("front_left_motor");
        c.frontRightName.set("front_right_motor");
        c.backLeftName.set("back_left_motor");
        c.backRightName.set("back_right_motor");
        c.frontLeftDirection.set(DcMotorSimple.Direction.FORWARD);
        c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
        c.backLeftDirection.set(DcMotorSimple.Direction.FORWARD);
        c.backRightDirection.set(DcMotorSimple.Direction.REVERSE);
    });
    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("pinpoint");
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        c.xPodOffset.set(-2.15145081046998);
        c.yPodOffset.set(-0.8476206261341965);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });



        public static ForesightConfig foresightConfig = new ForesightConfig(
                c -> {
                    Controller primaryTranslationalForward = Controller.proportional(0.18402900994735133);
                    Controller secondaryTranslationalForward = Controller.proportional(0.06799380182983647);
                    Controller primaryTranslationalLateral = Controller.proportional(0.25264554570995484);
                    Controller secondaryTranslationalLateral = Controller.proportional(0.09334577832651543);

                    c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                    c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                    c.coast.set(Controller.proportionalFeedforward(0.01637701348373703));
                    c.brake.set(Controller.proportionalFeedforward(0.013920461461176474));

                    c.headingFeedback.set(Controller.proportional(4.645651233793986));
                    c.headingBrakeCoefficients.set(Vector2D.cartesian(0.05110403557646733, 0.003051000092483762));

                    c.linearBrakeCoefficients.set(Matrix.diag(0.07168655642344036, 0.05725807348480756));
                    c.quadraticBrakeCoefficients.set(Matrix.diag(0.0011534297597258398, 0.0011194887495136027));

                    c.maxAchievableForwardVelocity.set(63.99871167707321);
                    c.maxAchievableStrafeVelocity.set(54.65889833332592);
                    c.naturalForwardDeceleration.set(33.629923307949255);
                    c.naturalStrafeDeceleration.set(57.442899974289);
                }
        );
    }