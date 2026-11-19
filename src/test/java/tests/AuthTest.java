package tests;

import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class AuthTest {

    private final String BASE_URL = "http://localhost:8080";
    private final String VALID_TOKEN = "Bearer secret-token-123";

    @Test
    public void testAccessWithoutToken() {
        // Негативный тест: пытаемся получить доступ без токена
        given()
                .baseUri(BASE_URL)
                .when()
                .get("/api/secure-data")
                .then()
                .statusCode(401) // Ожидаем 401 Unauthorized
                .body("error", equalTo("Unauthorized"));
    }

    @Test
    public void testAccessWithInvalidToken() {
        // Негативный тест: пытаемся получить доступ с неправильным токеном
        given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer WRONG-TOKEN")
                .when()
                .get("/api/secure-data")
                .then()
                .statusCode(401);
    }

    @Test
    public void testAccessWithValidToken() {
        // Позитивный тест: правильный токен дает доступ
        given()
                .baseUri(BASE_URL)
                .header("Authorization", VALID_TOKEN)
                .when()
                .get("/api/secure-data")
                .then()
                .statusCode(200)
                .body("message", equalTo("Access granted"))
                .body("user", equalTo("admin"));
    }
}