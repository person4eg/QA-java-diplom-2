package user;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import pojo.UserData;

import static org.hamcrest.CoreMatchers.equalTo;
import static request.UserRequest.deleteUser;
import static request.UserRequest.registrationUser;

public class UserRegistrationTest {

    String token;

    @Test
    @DisplayName("Проверка регистрации нового пользователя")
    public void registrationUserTest() {
        UserData userData = new UserData("person4eg@mail.ru", "123456", "person4eg");
        Response response = registrationUser(userData);
        response.then()
                .assertThat()
                .body("success", equalTo(true))
                .statusCode(200);

        token = response.then().extract().path("accessToken");
    }

    @Test
    @DisplayName("Проверка регистрации существующего пользователя")
    public void registrationSameUserTest() {
        UserData userData = new UserData("person4eg@mail.ru", "123456", "person4eg");
        token = registrationUser(userData).then().extract().path("accessToken");

        Response response = registrationUser(userData);
        response.then().assertThat()
                .body("message", equalTo("User already exists"))
                .statusCode(403);
    }

    @Test
    @DisplayName("Проверка регистрации пользователя без email")
    public void registrationUserWithoutEmailTest() {
        UserData userData = new UserData();
        userData.setEmail("123456");
        userData.setName("person4eg");
        Response response = registrationUser(userData);
        response.then()
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"))
                .statusCode(403);
    }

    @Test
    @DisplayName("Проверка регистрации пользователя без password")
    public void registrationUserWithoutPasswordTest() {
        UserData userData = new UserData();
        userData.setEmail("person4eg@mail.ru");
        userData.setName("person4eg");
        Response response = registrationUser(userData);
        response.then()
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"))
                .statusCode(403);
    }

    @Test
    @DisplayName("Проверка регистрации пользователя без name")
    public void registrationUserWithoutNameTest() {
        UserData userData = new UserData();
        userData.setEmail("person4eg@mail.ru");
        userData.setName("123456");
        Response response = registrationUser(userData);
        response.then()
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"))
                .statusCode(403);
    }

    @After
    public void deleteUserData() {
        if (token != null) {
            deleteUser(token);
        }
    }
}
