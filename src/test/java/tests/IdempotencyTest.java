package tests;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import static org.testng.Assert.assertEquals;

public class IdempotencyTest {

    private final String BOOTSTRAP_SERVERS = "localhost:9092";
    private final String TOPIC = "interview-topic";

    // Настройки БД
    private final String DB_URL = "jdbc:postgresql://localhost:5432/mydb";
    private final String DB_USER = "postgres";
    private final String DB_PASS = "mysecretpassword";

    @Test
    public void testIdempotencyHandler() throws Exception {
        String orderId = "ORDER-999";

        // 1. Отправляем сообщение первый раз
        sendKafkaMessage(orderId, "Привет из первого сообщения");

        // 2. Имитируем обработчик: записываем в БД (успешно)
        boolean firstAttempt = processInDatabase(orderId);
        assertEquals(firstAttempt, true, "Первая обработка должна быть успешной");

        // 3. Имитируем повторную доставку (Kafka at-least-once)
        sendKafkaMessage(orderId, "Привет из дубля");

        // 4. Имитируем обработчик второй раз (должен упасть из-за уникального ключа)
        boolean secondAttempt = processInDatabase(orderId);
        assertEquals(secondAttempt, false, "Вторая обработка должна быть заблокирована БД");

        // 5. Проверяем, что в БД ровно одна запись
        int count = countOrdersInDb(orderId);
        assertEquals(count, 1, "В БД должен быть только один заказ, несмотря на дубль в Kafka!");

        System.out.println("Тест пройден! Дубль сообщения не создал дубль в БД.");
    }

    // Метод отправки в Kafka
    private void sendKafkaMessage(String key, String value) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            producer.send(new ProducerRecord<>(TOPIC, key, value));
            producer.flush();
        }
    }

    // Имитация сервиса: пытается записать заказ. Возвращает true, если получилось.
    private boolean processInDatabase(String orderId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement()) {
            // Попытка вставки
            stmt.executeUpdate("INSERT INTO processed_orders (order_id, status) VALUES ('" + orderId + "', 'PROCESSED')");
            return true;
        } catch (Exception e) {
            // Если ключ уже существует, база выдаст ошибку (duplicate key)
            System.out.println("Ожидаемая ошибка при дубле: " + e.getMessage());
            return false;
        }
    }

    // Проверка количества записей
    private int countOrdersInDb(String orderId) throws Exception {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS cnt FROM processed_orders WHERE order_id = '" + orderId + "'")) {
            rs.next();
            return rs.getInt("cnt");
        }
    }
}