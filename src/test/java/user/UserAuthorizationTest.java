package user;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import pojo.UserData;

import static org.hamcrest.CoreMatchers.equalTo;
import static request.UserRequest.*;

public class UserAuthorizationTest {

    String token;

    @Test
    @DisplayName("Проверка авторизации зарегистрированного пользователя")
    public void registrationUserTest() {
        UserData userData = new UserData("person4eg@mail.ru", "123456", "person4eg");
        registrationUser(userData).then().statusCode(200);

        Response response = authorizationUser(new UserData("person4eg@mail.ru", "123456"));
        response.then()
                .assertThat()
                .body("success", equalTo(true))
                .statusCode(200);

        token = response.then().extract().path("accessToken");
    }

    @Test
    @DisplayName("Проверка авторизации незарегистрированного пользователя")
    public void registrationUserWithWrongDataTest() {
        Response response = authorizationUser(new UserData("wrongEmail", "wrongPassword"));
        response.then()
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"))
                .statusCode(401);
    }

    @After
    public void deleteUserData() {
        if (token != null) {
            deleteUser(token);
        }
    }
}
