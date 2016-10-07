import Endpoints.CreateUserEndpoint;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

public final class CreateUserTests extends CreateUserEndpoint {

    private static final String CONSUMER_KEY = "37ea6ca88819450709faeced981f72b77442393d";
    private static final String CONSUMER_SECRET = "6426899e19aeb1165fd9222b0502a8c3c65c9d0e";
    private static final String SIGNATURE_METHOD = "PLAINTEXT";


    @BeforeClass
    public static void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }


    /* Scenario: Register a new user
    Given Endpoint "create new user" with url http://www.kiqplan.com/api/v2/users
     When request with unique email is send
     Then new user is created
      And status code 201 (Created) is returned
  */
    @Test
    public void createNewUser() {

        given()
                .when()
                .header("authorization", buildHeader(CONSUMER_KEY, CONSUMER_SECRET, SIGNATURE_METHOD))
                .contentType(ContentType.TEXT)
                .body(buildUserBody(createUniqueEmail(), "", "Ivan", "Mitkov", ""))
                .post("users")
                .then()
                .statusCode(201);
    }

    /* Scenario: Register a new user
  Given Endpoint "create new user" with url http://www.kiqplan.com/api/v2/users
   When request with NOT unique email is send
   Then new user should not be created
    And status code 400 (Bad Request)
    And error message "email not unique" are returned
  */
    @Test
    public void createUserWithNotUniqueEmail() {

        String notUniqueEmail = createUniqueEmail();


        given()
                .when()
                .header("authorization", buildHeader(CONSUMER_KEY, CONSUMER_SECRET, SIGNATURE_METHOD))
                .contentType(ContentType.TEXT)
                .body(buildUserBody(createUniqueEmail(), "", "Ivan", "Mitkov", ""))
                .post("users")
                .then()
                .statusCode(201);

        given()
                .when()
                .header("authorization", buildHeader(CONSUMER_KEY, CONSUMER_SECRET, SIGNATURE_METHOD))
                .contentType(ContentType.TEXT)
                .body(buildUserBody(notUniqueEmail, "", "Ivan", "Mitkov", ""))
                .post("users")
                .then()
                .statusCode(400)
                .assertThat()
                .body("meta.error.message", containsString("email not unique"));
    }

    /* Scenario: Register a new user
 Given Endpoint "create new user" with url http://www.kiqplan.com/api/v2/users
  When request with NOT unique email is send
  Then new user should not be created
   And status code 400 (Bad Request)
   And error message "Invalid 'email' field" are returned
 */
    @Test
    public void createUserWithInvalidEmail() {
        String uniqueEmailButInvalid = "email" + (System.currentTimeMillis() / 1000);

        given()
                .when()
                .header("authorization", buildHeader(CONSUMER_KEY, CONSUMER_SECRET, SIGNATURE_METHOD))
                .contentType(ContentType.TEXT)
                .body(buildUserBody(uniqueEmailButInvalid, "", "Ivan", "Mitkov", ""))
                .post("users")
                .then()
                .statusCode(400)
                .assertThat()
                .body("meta.error.message", containsString("Invalid 'email' field"));
    }


}
