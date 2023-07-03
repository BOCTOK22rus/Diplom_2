package user;

import io.restassured.response.Response;
import specs.RequestSpec;

import static specs.Constants.*;
import static io.restassured.RestAssured.given;

public class UserClient extends RequestSpec {

    public Response createUser(String email, String password, String name) {
        User user = new User(email, password, name);
        return given()
                .spec(getSpec())
                .body(user)
                .when()
                .post(CREATE_USER);
    }

    public Response loginUser(String email, String password) {
        Login login = new Login(email, password);
        return given()
                .spec(getSpec())
                .body(login)
                .when()
                .post(LOGIN_USER);
    }

    public Response changeUserData(String email, String name, String accessToken) {
        ChangeData changeData = new ChangeData(email, name, accessToken);
        return given()
                .spec(getSpec())
                .body(changeData)
                .auth().oauth2(accessToken)
                .when()
                .patch(USER);
    }

    public Response changeUserDataNotAuthorized(String email, String name) {
        ChangeDataNotAuthorized changeDataNotAuthorized = new ChangeDataNotAuthorized(email, name);
        return given()
                .spec(getSpec())
                .body(changeDataNotAuthorized)
                .when()
                .patch(USER);
    }

    public void deleteUser(String accessToken) {
        given()
                .spec(getSpec())
                .auth().oauth2(accessToken)
                .delete(USER);
    }
}