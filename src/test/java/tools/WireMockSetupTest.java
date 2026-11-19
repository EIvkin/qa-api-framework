package tools;

import io.restassured.http.ContentType;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;

public class WireMockSetupTest {

    @Test
    public void setupMockRules() {
        // 1. Правило: Запрос БЕЗ токена -> 401
        given()
                .baseUri("http://localhost:8080")
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"request\": {\n" +
                        "    \"method\": \"GET\",\n" +
                        "    \"url\": \"/api/secure-data\"\n" +
                        "  },\n" +
                        "  \"response\": {\n" +
                        "    \"status\": 401,\n" +
                        "    \"headers\": {\"Content-Type\": \"application/json\"},\n" +  // <-- ДОБАВЛЕНО
                        "    \"jsonBody\": {\"error\": \"Unauthorized\"}\n" +
                        "  }\n" +
                        "}")
                .when()
                .post("/__admin/mappings")
                .then().statusCode(201);

        // 2. Правило: Запрос С валидным токеном -> 200
        given()
                .baseUri("http://localhost:8080")
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"request\": {\n" +
                        "    \"method\": \"GET\",\n" +
                        "    \"url\": \"/api/secure-data\",\n" +
                        "    \"headers\": {\n" +
                        "      \"Authorization\": {\n" +
                        "        \"equalTo\": \"Bearer secret-token-123\"\n" +
                        "      }\n" +
                        "    }\n" +
                        "  },\n" +
                        "  \"response\": {\n" +
                        "    \"status\": 200,\n" +
                        "    \"headers\": {\"Content-Type\": \"application/json\"},\n" + // <-- ДОБАВЛЕНО
                        "    \"jsonBody\": {\"message\": \"Access granted\", \"user\": \"admin\"}\n" +
                        "  }\n" +
                        "}")
                .when()
                .post("/__admin/mappings")
                .then().statusCode(201);

        System.out.println("Мок-правила успешно созданы!");
    }
}