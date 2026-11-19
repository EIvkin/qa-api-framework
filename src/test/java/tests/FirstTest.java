package tests;

import base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class FirstTest extends BaseTest {

    @Test
    public void testOpenSearchSchema() {
        Response response = given()
                .when()
                .get();

        // Проверяем статус-код
        response.then().statusCode(200);

        // Проверяем, что ответ соответствует JSON-схеме
        response.then().body(matchesJsonSchemaInClasspath("openSearchSchema.json"));

        System.out.println("Схема валидна! Кластер: " + response.jsonPath().getString("cluster_name"));
    }
}