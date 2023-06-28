import data.user.UserClient;
import org.apache.commons.lang3.RandomStringUtils;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.*;

import static data.Constants.DOMAIN_EMAIL;
import static org.hamcrest.Matchers.equalTo;

public class CreateUserTest {

    UserClient userClient = new UserClient();
    private final String email = RandomStringUtils.randomAlphabetic(6) + DOMAIN_EMAIL;
    private final String password = RandomStringUtils.randomAlphabetic(8);
    private final String name = RandomStringUtils.randomAlphabetic(8);

    @Test
    @DisplayName("Check Create User")
    @Description("Тест проверяет метод создания пользователя")
    public void checkCreateUser() {
        Response response = userClient.createUser(email, password, name);
        response.then().assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
        String accessToken = response.jsonPath().getString("accessToken").substring(7);
        userClient.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Check Cannot Create Existing User")
    @Description("Тест проверяет метод создания пользователя с существующим логином")
    public void checkCannotCreateExistingUser() {
        Response response = userClient.createUser(email, password, name);
        Response secondResponse = userClient.createUser(email, password, name);
        secondResponse.then().assertThat()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
        String accessToken = response.jsonPath().getString("accessToken").substring(7);
        userClient.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Check Field Email")
    @Description("Тест проверяет обязательность поля email при создании пользователя")
    public void checkFieldEmail() {
        Response response = userClient.createUser("", password, name);
        response.then().assertThat()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Check Field Password")
    @Description("Тест проверяет обязательность поля password при создании пользователя")
    public void checkFieldPassword() {
        Response response = userClient.createUser(email, "", name);
        response.then().assertThat()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}