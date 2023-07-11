import user.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import static specs.Constants.DOMAIN_EMAIL;
import static org.hamcrest.Matchers.equalTo;

public class LoginUserTest {

    UserClient userClient = new UserClient();
    private final String email = RandomStringUtils.randomAlphabetic(6) + DOMAIN_EMAIL;
    private final String password = RandomStringUtils.randomAlphabetic(8);
    private final String name = RandomStringUtils.randomAlphabetic(8);

    @Test
    @DisplayName("Check Login User")
    @Description("Тест проверяет авторизацию пользователя")
    public void checkLoginUser() {
        userClient.createUser(email, password, name);
        Response response = userClient.loginUser(email, password);
        response.then().assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
        String accessToken = response.jsonPath().getString("accessToken").substring(7);
        userClient.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Check Field Login")
    @Description("Тест проверяет валидацию значения поля login при авторизации пользователя")
    public void checkFieldEmail() {
        Response response = userClient.createUser(email, password, name);
        Response secondResponse = userClient.loginUser(email.substring(1), password);
        secondResponse.then().assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
        String accessToken = response.jsonPath().getString("accessToken").substring(7);
        userClient.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Check Field Password")
    @Description("Тест проверяет валидацию значения поля password при авторизации пользователя")
    public void checkFieldPassword() {
        Response response = userClient.createUser(email, password, name);
        Response secondResponse = userClient.loginUser(email, password.substring(1));
        secondResponse.then().assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
        String accessToken = response.jsonPath().getString("accessToken").substring(7);
        userClient.deleteUser(accessToken);
    }
}