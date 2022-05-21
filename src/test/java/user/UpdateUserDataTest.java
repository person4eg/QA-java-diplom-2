package user;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import pojo.UserData;

import static org.hamcrest.CoreMatchers.equalTo;
import static request.UserRequest.*;

public class UpdateUserDataTest {

    String token;

    @Test
    @DisplayName("Проверка измененния данных авторизованного пользователя")
    public void updateUserWithAuthorizationTest() {
        UserData userData = new UserData("person4eg@mail.ru", "123456", "person4eg");
        registrationUser(userData).then().statusCode(200);
        token = authorizationUser(new UserData("person4eg@mail.ru", "123456")).then().extract().path("accessToken");

        Response response = updateUserData(new UserData("newPerson4eg@mail.ru", "new123456", "newPerson4eg"), token);
        response.then()
                .assertThat()
                .body("success", equalTo(true))
                .statusCode(200);

        authorizationUser(new UserData("newPerson4eg@mail.ru", "new123456")).then()
                .assertThat()
                .body("success", equalTo(true))
                .statusCode(200);
    }
    @Test
    @DisplayName("Проверка измененния данных неавторизиронного пользователя")
    public void updateUserWithoutAuthorizationTest() {

        Response response = updateUserData(new UserData(null, null, null));
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
}
