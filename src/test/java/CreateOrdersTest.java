import com.google.gson.*;
import orders.OrdersClient;
import user.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import java.util.List;

import static specs.Constants.DOMAIN_EMAIL;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateOrdersTest {

    OrdersClient ordersClient = new OrdersClient();
    UserClient userClient = new UserClient();
    private final String email = RandomStringUtils.randomAlphabetic(6) + DOMAIN_EMAIL;
    private final String password = RandomStringUtils.randomAlphabetic(8);
    private final String name = RandomStringUtils.randomAlphabetic(8);

    @Test
    @DisplayName("Check Create Orders Not Auth")
    @Description("Тест проверяет создание заказа без авторизации")
    public void checkCreateOrdersNotAuth() {
        Response responseIngredients = ordersClient.getAllIngredients();
        List<JsonElement> ingredients = responseIngredients.jsonPath().get("data._id");
        String requestBody = ordersClient.getBodyOrders(ingredients);
        Response responseOrders = ordersClient.createOrdersNotAuth(requestBody);
        responseOrders.then().assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order", notNullValue());
    }

    @Test
    @DisplayName("Check Create Orders Auth")
    @Description("Тест проверяет создание заказа c авторизацией")
    public void checkCreateOrdersAuth() {
        Response responseAuthUser = userClient.createUser(email, password, name);
        Response responseIngredients = ordersClient.getAllIngredients();
        List<JsonElement> ingredients = responseIngredients.jsonPath().get("data._id");
        String requestBody = ordersClient.getBodyOrders(ingredients);
        String accessToken = responseAuthUser.jsonPath().getString("accessToken").substring(7);
        Response responseOrders = ordersClient.createOrdersAuth(requestBody, accessToken);
        userClient.deleteUser(accessToken);
        responseOrders.then().assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.owner.email", equalTo(email.toLowerCase()));
    }

    @Test
    @DisplayName("Check Create Orders Not Ingredients")
    @Description("Тест проверяет создание заказа без ингредиентов")
    public void checkCreateOrdersNotIngredients() {
        String requestBody = ordersClient.getBodyOrdersNotIngredients();
        Response responseOrders = ordersClient.createOrdersNotAuth(requestBody);
        responseOrders.then().assertThat()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Check Create Orders Bad Ingredients")
    @Description("Тест проверяет создание заказа с неверным хешем ингредиентов")
    public void checkCreateOrdersBadIngredients() {
        Response responseIngredients = ordersClient.getAllIngredients();
        List<JsonElement> ingredients = responseIngredients.jsonPath().get("data._id");
        String requestBody = ordersClient.getBodyOrdersBadHash(ingredients);
        Response responseOrders = ordersClient.createOrdersNotAuth(requestBody);
        responseOrders.then().assertThat()
                .statusCode(500)
                .statusLine("HTTP/1.1 500 Internal Server Error");
    }
}