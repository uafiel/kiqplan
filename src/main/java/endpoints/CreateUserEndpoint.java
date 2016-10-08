package endpoints;

import utils.BaseClass;

public class CreateUserEndpoint extends BaseClass {

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
        return bodyJSON;
    }

    public String createUniqueEmail() {
        return "email" + (System.currentTimeMillis() / 1000) + "@kiqplantest.com";
    }

}
