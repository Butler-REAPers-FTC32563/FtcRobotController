package org.firstinspires.ftc.teamcode.mechanisms;

public class PIDcontroller {

    public double kp, ki, kd;

    double integral;

    double lastError;

    long lastTime;

    public PIDcontroller(double kp, double ki, double kd) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
    }

    public void reset() {
        integral = 0;
        lastError = 0;
        lastTime = System.nanoTime();
    }

    public double calculate(double error) {
        long now = System.nanoTime();
        // now is in nano seconds so covert to seconds for delta time
        double dt = (now - lastTime) / 1_000_000_000,0;
        if dt <= 0) dt = 0.001;
        lastTime = now;

        // error * delta so smaller errors with longer runtime add up the same as a big error over a short time
        integral += error * dt; // delta * error so small error for a long times adds up to a big error for a short time
        if (integral > 100) integral = 100;
        if (integral < -100) integral = -100;

        double derivative = (error - lastError) / dt; // measures rate of change
        lastError = error;

        return (kp * error) + (ki * integral) + (kd * derivative);
    }


}
