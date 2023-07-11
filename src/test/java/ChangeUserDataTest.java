import org.junit.After;
import org.junit.Before;
import user.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import static specs.Constants.DOMAIN_EMAIL;
import static org.hamcrest.Matchers.equalTo;

public class ChangeUserDataTest {

    UserClient userClient = new UserClient();

    private Response response;
    private String email;
    private String password;
    private String name;
    private String accessToken;
    private String newName;
    private String newEmail;

    @Before
    public void getUserData() {
        email = RandomStringUtils.randomAlphabetic(6) + DOMAIN_EMAIL;
        password = RandomStringUtils.randomAlphabetic(8);
        name = RandomStringUtils.randomAlphabetic(8);
    }

    @Test
    @DisplayName("Check Change User Name")
    @Description("Тест проверяет метод изменения name пользователя")
    public void checkChangeUserName() {
        response = userClient.createUser(email, password, name);
        accessToken = response.jsonPath().getString("accessToken").substring(7);
        newName = name.substring(1);
        Response secondResponse = userClient.changeUserData(email, newName, accessToken);
        secondResponse.then().assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email.toLowerCase()))
                .body("user.name", equalTo(newName));
    }

    @Test
    @DisplayName("Check Change User Email")
    @Description("Тест проверяет метод изменения email пользователя")
    public void checkChangeUserEmail() {
        response = userClient.createUser(email, password, name);
        accessToken = response.jsonPath().getString("accessToken").substring(7);
        newEmail = email.substring(1);
        Response secondResponse = userClient.changeUserData(newEmail, name, accessToken);
        secondResponse.then().assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(newEmail.toLowerCase()))
                .body("user.name", equalTo(name));
    }

    @Test
    @DisplayName("Check Change User Name Unauthorized")
    @Description("Тест проверяет метод изменения имени пользователя без авторизации")
    public void checkChangeUserNameUnauthorized() {
        response = userClient.createUser(email, password, name);
        accessToken = response.jsonPath().getString("accessToken").substring(7);
        newName = name.substring(1);
        Response secondResponse = userClient.changeUserDataNotAuthorized(email, newName);
        secondResponse.then().assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Check Change User Email Unauthorized")
    @Description("Тест проверяет метод изменения email пользователя без авторизации")
    public void checkChangeUserEmailUnauthorized() {
        response = userClient.createUser(email, password, name);
        accessToken = response.jsonPath().getString("accessToken").substring(7);
        newEmail = email.substring(1);
        Response secondResponse = userClient.changeUserDataNotAuthorized(newEmail, name);
        secondResponse.then().assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @After
    public void deleteUser() {
        userClient.deleteUser(accessToken);
    }
}