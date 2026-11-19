package kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.testng.annotations.Test;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import static org.testng.Assert.assertEquals;

public class KafkaTest {

    private final String BOOTSTRAP_SERVERS = "localhost:9092";
    private final String TOPIC = "interview-topic";

    @Test
    public void testSendAndReceiveMessage() {
        // 1. ОТПРАВКА СООБЩЕНИЯ (Producer)
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps)) {
            String message = "Привет, это тест из Java!";
            // Ключ (order_id) важен для гарантии порядка сообщений в партиции
            String key = "order-123";

            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, key, message);
            producer.send(record);
            producer.flush(); // Ждем, пока сообщение точно уйдет в брокер
            System.out.println("Сообщение отправлено: " + message);
        }

        // 2. ЧТЕНИЕ СООБЩЕНИЯ (Consumer)
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group-" + System.currentTimeMillis()); // Уникальная группа
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // Читаем с самого начала

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps)) {
            consumer.subscribe(Collections.singletonList(TOPIC));

            // Ждем получения сообщения (максимум 5 секунд)
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));

            boolean messageFound = false;
            for (ConsumerRecord<String, String> record : records) {
                if ("order-123".equals(record.key())) {
                    System.out.println("Получено сообщение: " + record.value());
                    assertEquals(record.value(), "Привет, это тест из Java!");
                    messageFound = true;
                    break;
                }
            }
            // Проверяем, что сообщение действительно было найдено
            assert messageFound : "Сообщение с ключом order-123 не найдено в топике!";
        }
    }
}