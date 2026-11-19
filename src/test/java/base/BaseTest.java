package base;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;

public class BaseTest {

    @BeforeClass
    public void setup() {
        // Здесь мы задаем базовый URL. В будущем его можно вынести в конфиг-файл.
        RestAssured.baseURI = "http://localhost:9200";
        // Включаем логирование только если тест упал (чтобы не засорять консоль)
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
}