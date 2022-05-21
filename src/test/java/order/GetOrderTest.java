package order;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import pojo.OrderData;
import pojo.UserData;
import request.OrderRequest;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static request.UserRequest.*;

public class GetOrderTest {

    String token;
    int orderNumber;

    @Test
    @DisplayName("Проверка получения заказа авторизованного пользователя")
    public void checkAuthorizedUserOrderTest() {
        authorize();
        orderNumber = OrderRequest.createOrder(new OrderData(List.of("61c0c5a71d1f82001bdaaa6d")), token).then().extract().path("order.number");

        Response response = OrderRequest.getOrderFromUser(token);
        response.then()
                .assertThat()
                .body("success", equalTo(true))
                .body("orders[0].ingredients[0]", equalTo("61c0c5a71d1f82001bdaaa6d"))
                .body("orders[0].number", equalTo(orderNumber))
                .statusCode(200);
    }

    @Test
    @DisplayName("Проверка получения заказа неавторизованного пользователя")
    public void checkUnauthorizedUserOrderTest() {
        authorize();
        orderNumber = OrderRequest.createOrder(new OrderData(List.of("61c0c5a71d1f82001bdaaa6d")), token).then().extract().path("order.number");

        Response response = OrderRequest.getOrderFromUser();
        response.then()
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"))
                .statusCode(401);
    }

    @After
    public void deleteUserData() {
        if (token != null) {
            deleteUser(token);
        }
    }

    private void authorize() {
        UserData userData = new UserData("person4eg@mail.ru", "123456", "person4eg");
        registrationUser(userData).then().statusCode(200);
        Response authorization = authorizationUser(new UserData("person4eg@mail.ru", "123456"));
        token = authorization.then().extract().path("accessToken");
        authorization.then().statusCode(200);
    }
}
