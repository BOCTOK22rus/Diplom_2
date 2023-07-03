import user.UserClient;
import org.apache.commons.lang3.RandomStringUtils;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.*;

import static specs.Constants.DOMAIN_EMAIL;
import static org.hamcrest.Matchers.equalTo;

public class CreateUserTest {

    UserClient userClient = new UserClient();

    private Response response;
    private String email;
    private String password;
    private String name;

    @Before
    public void getUserData() {
        email = RandomStringUtils.randomAlphabetic(6) + DOMAIN_EMAIL;
        password = RandomStringUtils.randomAlphabetic(8);
        name = RandomStringUtils.randomAlphabetic(8);
    }

    @Test
    @DisplayName("Check Create User")
    @Description("Тест проверяет метод создания пользователя")
    public void checkCreateUser() {
        response = userClient.createUser(email, password, name);
        String accessToken = response.jsonPath().getString("accessToken").substring(7);
        userClient.deleteUser(accessToken);
        response.then().assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Check Cannot Create Existing User")
    @Description("Тест проверяет метод создания пользователя с существующим логином")
    public void checkCannotCreateExistingUser() {
        response = userClient.createUser(email, password, name);
        String accessToken = response.jsonPath().getString("accessToken").substring(7);
        Response secondResponse = userClient.createUser(email, password, name);
        userClient.deleteUser(accessToken);
        secondResponse.then().assertThat()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Check Field Email")
    @Description("Тест проверяет обязательность поля email при создании пользователя")
    public void checkFieldEmail() {
        response = userClient.createUser("", password, name);
        response.then().assertThat()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Check Field Password")
    @Description("Тест проверяет обязательность поля password при создании пользователя")
    public void checkFieldPassword() {
        response = userClient.createUser(email, "", name);
        response.then().assertThat()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}