package endpoints;

import utils.BaseClass;

public class UserActivitiesEndpoint extends BaseClass {

    public String buildActivityBody(String activityId, String startDate, String endDate, String steps) {
        String bodyJSON = "{\n" +
                "  \"id\": 0,\n" +
                "  \"activity_id\":" + activityId + ",\n" +
                "  \"met_score\": \"float\",\n" +
                "  \"description\": \"\",\n" +
                "  \"category\": \"\",\n" +
                "  \"intensity_level\": \"\",\n" +
                "  \"start_date_time\": \"" + startDate + "\",\n" +
                "  \"end_date_time\": \"" + endDate + "\",\n" +
                "  \"total_duration\": 0,\n" +
                "  \"aerobic_duration\": 0,\n" +
                "  \"total_steps\": " + steps + ",\n" +
                "  \"aerobic_steps\": 0,\n" +
                "  \"distance_in_km\": 0,\n" +
                "  \"distance_in_miles\": 0,\n" +
                "  \"calories_burnt\": 0,\n" +
                "  \"fat_burnt\": 0,\n" +
                "  \"data_origin_id\": \"\",\n" +
                "  \"data_origin_name\": \"\",\n" +
                "  \"origin_activity_name\": \"\",\n" +
                "  \"origin_activity_type\": \"\",\n" +
                "  \"planner_flag\": 0,\n" +
                "  \"date_utc\": \"\",\n" +
                "  \"verified\": false\n" +
                "}";
        return bodyJSON;
    }



}
