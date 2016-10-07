package Endpoints;

import Utils.BaseClass;

public class UserMeasurementEndpoint extends BaseClass {

    public String buildMeasurumentBody(MeasurementType type, String value, String date, String unitSystem) {
        String bodyJSON = "{\n" +
                "  \"type\": \"" + type + "\",\n" +
                "  \"value\": \"" + value + "\",\n" +
                "  \"date\": \"" + date + "\",\n" +
                "  \"unit_system\": \"" + unitSystem + "\"\n" +
                "}";
        return bodyJSON;
    }

    public enum MeasurementType {
        DISTANCE,
        WEIGHT,
        HEIGHT,
        STRIDE,
        FATBURNED,
        WAIST,
        BLOODPRESURE,
        BMI
    }
}
