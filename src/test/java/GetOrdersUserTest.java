import com.google.gson.JsonElement;
import data.user.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import data.orders.OrdersClient;

import java.util.List;

import static data.Constants.*;
import static org.hamcrest.Matchers.equalTo;

public class GetOrdersUserTest {

    OrdersClient ordersClient = new OrdersClient();
    UserClient userClient = new UserClient();
    private final String email = RandomStringUtils.randomAlphabetic(6) + DOMAIN_EMAIL;
    private final String password = RandomStringUtils.randomAlphabetic(8);
    private final String name = RandomStringUtils.randomAlphabetic(8);

    @Test
    @DisplayName("Check Orders User Auth")
    @Description("Тест проверяет заказы пользователя с авторизацией")
    public void checkOrdersUserAuth() {
        Response responseAuthUser = userClient.createUser(email, password, name);
        Response responseIngredients = ordersClient.getAllIngredients();
        List<JsonElement> ingredients = responseIngredients.jsonPath().get("data._id");
        String requestBody = ordersClient.getBodyOrders(ingredients);
        String accessToken = responseAuthUser.jsonPath().getString("accessToken").substring(7);
        Response responseOrders = ordersClient.createOrdersAuth(requestBody, accessToken);
        int orderNumber = Integer.parseInt(responseOrders.jsonPath().getString("order.number"));
        Response responseOrdersUser = ordersClient.getOrdersUserAuth(accessToken);
        responseOrdersUser.then().assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders[0].number", equalTo(orderNumber));
        userClient.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Check Orders User Not Auth")
    @Description("Тест проверяет заказы пользователя без авторизации")
    public void checkOrdersUserNotAuth() {
        Response responseOrdersUser = ordersClient.getOrdersUserNotAuth();
        responseOrdersUser.then().assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}