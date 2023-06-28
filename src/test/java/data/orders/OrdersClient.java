package data.orders;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.restassured.response.Response;
import data.RequestSpec;

import java.util.List;
import java.util.Random;

import static data.Constants.*;
import static io.restassured.RestAssured.given;

public class OrdersClient extends RequestSpec {

    public Response createOrdersNotAuth(String ingredients) {
        return given()
                .spec(getSpec())
                .body(ingredients)
                .when()
                .post(ORDERS);
    }

    public Response createOrdersAuth(String ingredients, String accessToken) {
        return given()
                .spec(getSpec())
                .body(ingredients)
                .auth().oauth2(accessToken)
                .when()
                .post(ORDERS);
    }

    public Response getAllIngredients() {
        return given()
                .spec(getSpec())
                .when()
                .get(INGREDIENTS);
    }

    public Response getOrdersUserAuth(String accessToken) {
        return given()
                .spec(getSpec())
                .auth().oauth2(accessToken)
                .when()
                .get(ORDERS);
    }

    public Response getOrdersUserNotAuth() {
        return given()
                .spec(getSpec())
                .when()
                .get(ORDERS);
    }

    public String getBodyOrders(List<JsonElement> ingredients) {
        Random random = new Random();
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        String firstIngredient = String.valueOf(ingredients.get(random.nextInt(ingredients.size())));
        String secondIngredient = String.valueOf(ingredients.get(random.nextInt(ingredients.size())));
        String[] ingredientsArray = {firstIngredient, secondIngredient};
        for (String ingredient : ingredientsArray) {
            jsonArray.add(new JsonPrimitive(ingredient));
        }
        jsonObject.add("ingredients", jsonArray);
        return jsonObject.toString();
    }

    public String getBodyOrdersBadHash(List<JsonElement> ingredients) {
        Random random = new Random();
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        String firstIngredient = String.valueOf(ingredients.get(random.nextInt(ingredients.size()))).substring(1);
        String secondIngredient = String.valueOf(ingredients.get(random.nextInt(ingredients.size()))).substring(1);
        String[] ingredientsArray = {firstIngredient, secondIngredient};
        for (String ingredient : ingredientsArray) {
            jsonArray.add(new JsonPrimitive(ingredient));
        }
        jsonObject.add("ingredients", jsonArray);
        return jsonObject.toString();
    }

    public String getBodyOrdersNotIngredients() {
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        String[] ingredientsArray = {};
        for (String ingredients : ingredientsArray) {
            jsonArray.add(new JsonPrimitive(ingredients));
        }
        jsonObject.add("ingredients", jsonArray);
        return jsonObject.toString();
    }
}