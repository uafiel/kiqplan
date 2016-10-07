import Endpoints.CreateUserEndpoint;
import Endpoints.UserActivitiesEndpoint;
import Endpoints.UserMeasurementEndpoint;
import Utils.User;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;

public class UserActivitiesTests extends UserActivitiesEndpoint {

    private static final String CONSUMER_KEY = "37ea6ca88819450709faeced981f72b77442393d";
    private static final String CONSUMER_SECRET = "6426899e19aeb1165fd9222b0502a8c3c65c9d0e";
    private static final String SIGNATURE_METHOD = "PLAINTEXT";
    private static User user = new User();
    private CreateUserEndpoint userEndpoint = new CreateUserEndpoint();
    private UserMeasurementEndpoint measurementEndpoint = new UserMeasurementEndpoint();

    @BeforeClass
    public void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeMethod
    public void prepareTestData() {

        //create user that will be used for the test
        String responseBody =
                given()
                        .header("authorization", buildHeader(CONSUMER_KEY, CONSUMER_SECRET, SIGNATURE_METHOD))
                        .contentType(ContentType.TEXT)
                        .body(userEndpoint.buildUserBody(userEndpoint.createUniqueEmail(), "", "", "", ""))
                        .post("users")
                        .asString();

        user.userId = from(responseBody).getString("data.guid");
        user.token = from(responseBody).getString("data.token");
        user.secret = from(responseBody).getString("data.secret");


    }

    /* Scenario: Add user activity
  Given "Add user activity" endpoint with url http://www.kiqplan.com/api/v2/users/me/activities/?override=false
    And an existing user that has weight filled in the system
   When request is made
   Then activity should be added
    And status code 201 (Created) should be returned
  */
    @Test
    public void addUserActivity() {
        given()
                .when()
                .header("authorization", buildHeaderWithUserTokens(CONSUMER_KEY, CONSUMER_SECRET, SIGNATURE_METHOD, user.token, user.secret))
                .contentType(ContentType.TEXT)
                .body(measurementEndpoint.buildMeasurumentBody(UserMeasurementEndpoint.MeasurementType.WEIGHT, "87", "2016-09-04T13:00:42", "kg"))
                .post("users/me/measurements/")
                .then()
                .statusCode(201);

        given()
                .when()
                .header("authorization", buildHeaderWithUserTokens(CONSUMER_KEY, CONSUMER_SECRET, SIGNATURE_METHOD, user.token, user.secret))
                .contentType(ContentType.TEXT)
                .body(buildActivityBody("1", "2016-09-05T11:00:00", "2016-09-05T15:00:00", "5"))
                .post("users/me/activities/?override=false")
                .then()
                .statusCode(201);

    }

    /* Scenario: Attempt to add activity for a user that has no weight set
  Given "Add user activity" endpoint with url http://www.kiqplan.com/api/v2/users/me/activities/?override=false
    And an existing user that has NO weight set in the system
   When request is made
   Then activity should NOT be added
    And status code 400 (Bad Request)
    And message "No weight set in the system"
    And user_message "Please log your weight" should be returned
  */
    @Test
    public void addUserActivityWithNoWeightSet() {

        given()
                .when()
                .header("authorization", buildHeaderWithUserTokens(CONSUMER_KEY, CONSUMER_SECRET, SIGNATURE_METHOD, user.token, user.secret))
                .contentType(ContentType.TEXT)
                .body(buildActivityBody("1", "2016-09-05T11:00:00", "2016-09-05T15:00:00", "5"))
                .post("users/me/activities/?override=false")
                .then()
                .statusCode(400)
                .assertThat()
                .body("meta.error.message", containsString("No weight set in the system"))
                .body("meta.error.user_message", containsString("Please log your weight"));

    }

    /* Scenario: Attempt to add activity for a user using invalid user token
  Given "Add user activity" endpoint with url http://www.kiqplan.com/api/v2/users/me/activities/?override=false
    And an existing user
    And invalid token
   When request is made
   Then activity should NOT be added
    And status code 400 (Bad Request)
    And message "oauth_problem=token_rejected" should be returned.
  */
    @Test
    public void addUserActivityWithInvalidUserToken() {

        given()
                .when()
                .header("authorization", buildHeaderWithUserTokens(CONSUMER_KEY, CONSUMER_SECRET, SIGNATURE_METHOD, "invalid", user.secret))
                .contentType(ContentType.TEXT)
                .body(buildActivityBody("1", "2016-09-05T11:00:00", "2016-09-05T15:00:00", "5"))
                .post("users/me/activities/?override=false")
                .then()
                .statusCode(400)
                .assertThat()
                .body("meta.error.message", containsString("oauth_problem=token_rejected"));


    }

    /* Scenario: Attempt to add activity for a user using invalid user secret
  Given "Add user activity" endpoint with url http://www.kiqplan.com/api/v2/users/me/activities/?override=false
    And an existing user
    And invalid secret
   When request is made
   Then activity should NOT be added
    And status code 400 (Bad Request)
    And message that contains "oauth_problem=signature_invalid" should be returned.
  */
    @Test
    public void addUserActivityWithInvalidUserSecret() {
        given()
                .when()
                .header("authorization", buildHeaderWithUserTokens(CONSUMER_KEY, CONSUMER_SECRET, SIGNATURE_METHOD, user.token, "invalid"))
                .contentType(ContentType.TEXT)
                .body(buildActivityBody("1", "2016-09-05T11:00:00", "2016-09-05T15:00:00", "5"))
                .post("users/me/activities/?override=false")
                .then()
                .statusCode(400)
                .assertThat()
                .body("meta.error.message", anything("oauth_problem=signature_invalid"));
    }

    /* Scenario: Attempt to duplicate activity
 Given "Add user activity" endpoint with url http://www.kiqplan.com/api/v2/users/me/activities/?override=false
   And an existing user
   And existing user activity
  When request to add the same activity is made
  Then activity should NOT be added
   And status code 400 (Bad Request)
   And message "An activity already exists in the specified date range" should be returned.
 */
    @Test
    public void addDuplicateUserActivity() {
        given()
                .when()
                .header("authorization", buildHeaderWithUserTokens(CONSUMER_KEY, CONSUMER_SECRET, SIGNATURE_METHOD, user.token, user.secret))
                .contentType(ContentType.TEXT)
                .body(measurementEndpoint.buildMeasurumentBody(UserMeasurementEndpoint.MeasurementType.WEIGHT, "87", "2016-09-04T13:00:42", "kg"))
                .post("users/me/measurements/")
                .then()
                .statusCode(201);

        given()
                .when()
                .header("authorization", buildHeaderWithUserTokens(CONSUMER_KEY, CONSUMER_SECRET, SIGNATURE_METHOD, user.token, user.secret))
                .contentType(ContentType.TEXT)
                .body(buildActivityBody("1", "2016-09-05T11:00:00", "2016-09-05T15:00:00", "5"))
                .post("users/me/activities/?override=false")
                .then()
                .statusCode(201);

        given()
                .when()
                .header("authorization", buildHeaderWithUserTokens(CONSUMER_KEY, CONSUMER_SECRET, SIGNATURE_METHOD, user.token, user.secret))
                .contentType(ContentType.TEXT)
                .body(buildActivityBody("1", "2016-09-05T11:00:00", "2016-09-05T15:00:00", "5"))
                .post("users/me/activities/?override=false")
                .then()
                .statusCode(400)
                .assertThat()
                .body("meta.error.message", containsString("An activity already exists in the specified date range"))
                .body("meta.error.user_message", containsString("You have already added an activity for the time selected. Please edit time of activity and then try adding the activity again."));
    }


    /* Scenario: Attempt to add user activity with wrong date
 Given "Add user activity" endpoint with url http://www.kiqplan.com/api/v2/users/me/activities/?override=false
   And an existing user that has weight filled in the system
  When request is made with end_date_time before start_date_time
  Then activity should NOT be added
   And status code 400 (Bad Request)
   And message "end_date_time must be greater than start_date_time" should be returned.
 */
    @Test
    public void addActivityWithEndDateBeforeStartDate() {
        given()
                .when()
                .header("authorization", buildHeaderWithUserTokens(CONSUMER_KEY, CONSUMER_SECRET, SIGNATURE_METHOD, user.token, user.secret))
                .contentType(ContentType.TEXT)
                .body(measurementEndpoint.buildMeasurumentBody(UserMeasurementEndpoint.MeasurementType.WEIGHT, "87", "2016-09-04T13:00:42", "kg"))
                .post("users/me/measurements/")
                .then()
                .statusCode(201);

        String startDate = "2016-09-05T11:00:00";
        String endDate = "2016-09-05T10:59:59";

        given()
                .when()
                .header("authorization", buildHeaderWithUserTokens(CONSUMER_KEY, CONSUMER_SECRET, SIGNATURE_METHOD, user.token, user.secret))
                .contentType(ContentType.TEXT)
                .body(buildActivityBody("1", startDate, endDate, "5"))
                .post("users/me/activities/?override=false")
                .then()
                .statusCode(400)
                .assertThat()
                .body("meta.error.message", containsString("end_date_time must be greater than start_date_time"));

    }



    /* Scenario: Attempt to add user activity using invalid activity_id
 Given "Add user activity" endpoint with url http://www.kiqplan.com/api/v2/users/me/activities/?override=false
   And an existing user that has weight filled in the system
  When request is made with invalid activity_id
  Then activity should NOT be added
   And status code 400 (Bad Request)
   And message "Missing or invalid request body data" should be returned.
 */
    @Test
    public void addActivityInvalidActivityId() {
        given()
                .when()
                .header("authorization", buildHeaderWithUserTokens(CONSUMER_KEY, CONSUMER_SECRET, SIGNATURE_METHOD, user.token, user.secret))
                .contentType(ContentType.TEXT)
                .body(measurementEndpoint.buildMeasurumentBody(UserMeasurementEndpoint.MeasurementType.WEIGHT, "87", "2016-09-04T13:00:42", "kg"))
                .post("users/me/measurements/")
                .then()
                .statusCode(201);

        String invalidId = "id";

        given()
                .when()
                .header("authorization", buildHeaderWithUserTokens(CONSUMER_KEY, CONSUMER_SECRET, SIGNATURE_METHOD, user.token, user.secret))
                .contentType(ContentType.TEXT)
                .body(buildActivityBody(invalidId, "2016-09-04T13:00:42", "2016-09-04T19:00:42", "5"))
                .post("users/me/activities/?override=false")
                .then()
                .statusCode(400)
                .assertThat()
                .body("meta.error.message", containsString("Missing or invalid request body data"));

    }





}

