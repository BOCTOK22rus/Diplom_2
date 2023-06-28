import data.user.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import static data.Constants.DOMAIN_EMAIL;
import static org.hamcrest.Matchers.equalTo;

public class ChangeUserDataTest {

    UserClient userClient = new UserClient();
    private final String email = RandomStringUtils.randomAlphabetic(6) + DOMAIN_EMAIL;
    private final String password = RandomStringUtils.randomAlphabetic(8);
    private final String name = RandomStringUtils.randomAlphabetic(8);

    @Test
    @DisplayName("Check Change User Name")
    @Description("Тест проверяет метод изменения name пользователя")
    public void checkChangeUserName() {
        Response response = userClient.createUser(email, password, name);
        String accessToken = response.jsonPath().getString("accessToken").substring(7);
        String newName = name.substring(1);
        Response secondResponse = new UserClient().changeUserData(email, newName, accessToken);
        secondResponse.then().assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email.toLowerCase()))
                .body("user.name", equalTo(newName));
        new UserClient().deleteUser(accessToken);
    }

    @Test
    @DisplayName("Check Change User Email")
    @Description("Тест проверяет метод изменения email пользователя")
    public void checkChangeUserEmail() {
        Response response = userClient.createUser(email, password, name);
        String accessToken = response.jsonPath().getString("accessToken").substring(7);
        String newEmail = email.substring(1);
        Response secondResponse = userClient.changeUserData(newEmail, name, accessToken);
        secondResponse.then().assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(newEmail.toLowerCase()))
                .body("user.name", equalTo(name));
        new UserClient().deleteUser(accessToken);
    }

    @Test
    @DisplayName("Check Change User Name Unauthorized")
    @Description("Тест проверяет метод изменения имени пользователя без авторизации")
    public void checkChangeUserNameUnauthorized() {
        Response response = userClient.createUser(email, password, name);
        String accessToken = response.jsonPath().getString("accessToken").substring(7);
        String newName = name.substring(1);
        Response secondResponse = userClient.changeUserDataNotAuthorized(email, newName);
        secondResponse.then().assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
        new UserClient().deleteUser(accessToken);
    }

    @Test
    @DisplayName("Check Change User Email Unauthorized")
    @Description("Тест проверяет метод изменения email пользователя без авторизации")
    public void checkChangeUserEmailUnauthorized() {
        Response response = userClient.createUser(email, password, name);
        String accessToken = response.jsonPath().getString("accessToken").substring(7);
        String newEmail = email.substring(1);
        Response secondResponse = userClient.changeUserDataNotAuthorized(newEmail, name);
        secondResponse.then().assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
        new UserClient().deleteUser(accessToken);
    }
}