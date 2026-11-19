package tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class LogSearchTest {

    @Test
    public void testFindLogByTraceId() {
        // Настраиваем адрес нашего OpenSearch
        RestAssured.baseURI = "http://localhost:9200";

        // Формируем JSON-запрос для поиска (аналог того, что мы вводим в Kibana)
        // Мы ищем в индексе "logs" документы, где поле traceId равно "abc-123-xyz"
        String searchQuery = "{\n" +
                "  \"query\": {\n" +
                "    \"match\": {\n" +
                "      \"traceId\": \"abc-123-xyz\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        // Отправляем POST-запрос на поиск
        Response response = given()
                .contentType(ContentType.JSON)
                .body(searchQuery)
                .when()
                .post("/logs/_search");

        // Проверяем, что запрос прошел успешно
        response.then().statusCode(200);

        // Достаем из ответа количество найденных записей (поле hits.total.value)
        int totalHits = response.jsonPath().getInt("hits.total.value");
        System.out.println("Найдено логов: " + totalHits);

        // Проверяем, что нашли ровно 1 запись
        assertEquals(totalHits, 1, "Должен быть найден ровно один лог!");

        // Достаем само сообщение из лога, чтобы убедиться, что это тот самый лог
        String message = response.jsonPath().getString("hits.hits[0]._source.message");
        System.out.println("Текст лога: " + message);

        // Проверяем, что в логе написано про Timeout
        assertTrue(message.contains("Timeout"), "В логе должно быть слово Timeout");
    }
}