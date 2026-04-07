package org.firstinspires.ftc.teamcode;

public class RobotLocationPractice {

    double angle;
    double getAngle;

    public double getAngle() {
        return this.angle;
    }

    // constructor method
    public RobotLocationPractice(double angle) {

        this.angle = angle;
    }

    public double getHeading() {
        // normalizes robot heading between -180 and 180
        // this helps calculate turn angles, especially when crossing the 0,360 boundary

        double angle = this.angle; // copy the angle of imu
        while (angle > 180) {
            angle -= 360; // subtract until in target range
        }
        while (angle <= -180) {
            angle += 360; // add until in target range
        }
        return angle; // return normalized value
    }

    public void turnRobot(double angleChange) {

        angle += angleChange;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    double x;

    double y;

    public void changeX(double changeAmount) {
        x += changeAmount;
    }

    public void changeY(double changeAmount) {
        y += changeAmount;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }



}
