package Utils;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class BaseClass {

    @BeforeClass
    public static void setup() {

        String basePath = System.getProperty("server.base");
        if (basePath == null) {
            basePath = "/api/v2/";
        }
        RestAssured.basePath = basePath;

        String baseHost = System.getProperty("server.host");
        if (baseHost == null) {
            baseHost = "http://www.kiqplan.com";
        }
        RestAssured.baseURI = baseHost;

    }

    public String buildHeader(String consumerKey, String consumerSecret, String signatureMethod) {
        String header = "OAuth " +
                "oauth_consumer_key=\"" + consumerKey + "\"" +
                ",oauth_signature_method=\"" + signatureMethod + "\"" +
                ",oauth_timestamp=\"" + getTimeStamp() + "\"" +
                ",oauth_nonce=\"" + buildNonce() + "\"" +
                ",oauth_version=\"1.0\"" +
                ",oauth_signature=\"" + consumerSecret + "%26\"";

        return header;
    }


    public String buildHeaderWithUserTokens(String consumerKey, String consumerSecret, String signatureMethod, String userToken, String userSecret) {
        String header = "OAuth " +
                "oauth_consumer_key=\"" + consumerKey + "\"" +
                ",oauth_token=\"" + userToken  + "\"" +
                ",oauth_signature_method=\"" + signatureMethod + "\"" +
                ",oauth_timestamp=\"" + getTimeStamp() + "\"" +
                ",oauth_nonce=\"" + buildNonce() + "\"" +
                ",oauth_version=\"1.0\"" +
                ",oauth_signature=\"" + consumerSecret + "%26" + userSecret + "\"";

        return header;
    }

    private String buildNonce() {
        int oauth_nonce = ((int) (Math.random() * 100000000));
        return String.valueOf(oauth_nonce);
    }

    private String getTimeStamp() {
        long timeStamp = (System.currentTimeMillis() / 1000);
        return String.valueOf(timeStamp);
    }

    public String buildUserBody(String email, String social_id, String firstName, String lastName, String nickName) {
        String bodyJSON = "{\n" +
                "  \"email\": \"" + email + "\",\n" +
                "  \"social_id\": \"" + social_id + "\",\n" +
                "  \"cognito_identity_id\": \"\",\n" +
                "  \"firstname\": \"" + firstName + "\",\n" +
                "  \"lastname\": \"" + lastName + "\",\n" +
                "  \"password\": \"\",\n" +
                "  \"nickname\": \"\",\n" +
                "  \"dob\": \"\",\n" +
                "  \"gender\": \"\",\n" +
                "  \"locale\": \"\",\n" +
                "  \"timezone\": \"\",\n" +
                "  \"profile_url\": \"\",\n" +
                "  \"country_code\": \"\"\n" +
                "}";
        return  bodyJSON;
    }
}
