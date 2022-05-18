package order;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import pojo.OrderData;
import pojo.UserData;
import request.OrderRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static request.UserRequest.*;

public class CreateOrderTest {

    String email = "person4eg@mail.ru";
    String password = "123456";
    String name = "person4eg";
    String token;

    @Test
    @DisplayName("Проверка создания заказа с авторизацией и с ингредиентами")
    public void createOrderWithAuthorizationAndWithIngredientsTest() {
        authorize();
        Response response = OrderRequest.createOrder(new OrderData(List.of("61c0c5a71d1f82001bdaaa6d")), token);
        response.then()
                .assertThat()
                .body("success", equalTo(true))
                .body("name", equalTo("Флюоресцентный бургер"))
                .body("order", notNullValue())
                .statusCode(200);
    }

    @Test
    @DisplayName("Проверка создания заказа с авторизацией и без ингредиентов")
    public void createOrderWithAuthorizationAndWithoutIngredientsTest() {
        authorize();
        Response response = OrderRequest.createOrder(new OrderData(new ArrayList<>()), token);
        response.then()
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"))
                .statusCode(400);
    }

    @Test
    @DisplayName("Проверка создания заказа с авторизацией неправильным ингредиентами")
    public void createOrderWithAuthorizationAndWithIncorrectIngredientIdTest() {
        authorize();
        Response response = OrderRequest.createOrder(new OrderData(List.of(UUID.randomUUID().toString())), token);
        response.then()
                .assertThat()
                .statusCode(500);
    }

    @Test
    @DisplayName("Проверка создания заказа без авторизации")
    public void createOrderWithoutAuthorizationTest() {
        Response response = OrderRequest.createOrder(new OrderData(List.of("61c0c5a71d1f82001bdaaa6d")));
        response.then()
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @After
    public void deleteUserData() {
        if (token != null) {
            deleteUser(token);
        }
    }

    private void authorize() {
        UserData userData = new UserData(email, password, name);
        registrationUser(userData).then().statusCode(200);
        Response authorization = authorizationUser(new UserData(email, password));
        token = authorization.then().extract().path("accessToken");
        authorization.then().statusCode(200);
    }
}
