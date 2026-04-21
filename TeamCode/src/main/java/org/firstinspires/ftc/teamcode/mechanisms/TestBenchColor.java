package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class TestBenchColor {
    NormalizedColorSensor colorSensor;

    public enum DetectedColor {
        RED,
        BLUE,
        YELLOW,
        UNKNOWN

    }

    public void init(HardwareMap hwMap) {
        colorSensor = hwMap.get(NormalizedColorSensor.class, "sensor_color"); // config name might be incorrect
        colorSensor.setGain(8); //set gain of sensor can turn for accuracy
    }

    public DetectedColor getDetectedColor(Telemetry telemetry) {
        NormalizedRGBA colors = colorSensor.getNormalizedColors(); // returns 4 values

        float normRed, normGreen, normBlue;
        normRed = colors.red / colors.alpha;
        normGreen = colors.green / colors.alpha;
        normBlue = colors.blue / colors.alpha;

        telemetry.addData("Red", normRed);
        telemetry.addData("Green", normGreen);
        telemetry.addData("Blue", normBlue);

        // TODO add if statements for specifics colors added

        /*
        Brogans colors tune for our colurs
        red, green, blue
        RED = >.35, <.3, <.3
        Yellow = >.5, >.9 < .6
        BLUE = <.2, <.5, >.5
         */

        if (normRed > 0.35 && normGreen < 0.3 && normBlue < 0.3) {
            return DetectedColor.RED;
        }
        else if (normRed > 0.5 && normGreen > 0.9 && normBlue < 0.6) {
            return DetectedColor.YELLOW;
        }
        else if (normRed < 0.2 && normGreen < 0.5 && normBlue > 0.5) {
            return DetectedColor.BLUE;
            
        }

        return DetectedColor.UNKNOWN;


    }


}
