package tests;

import org.testng.annotations.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TransactionTest {

    // Это адрес нашей локальной базы данных, логин и пароль
    private final String DB_URL = "jdbc:postgresql://localhost:5432/mydb";
    private final String DB_USER = "postgres";
    private final String DB_PASS = "mysecretpassword";

    @Test
    public void testConcurrentUpdate() throws Exception {
        // 1. ПЕРВЫЙ ПОЛЬЗОВАТЕЛЬ (Транзакция 1)
        // Открываем соединение с базой (как будто зашел первый пользователь)
        Connection conn1 = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        // Отключаем автоматическое сохранение (начинаем транзакцию вручную)
        conn1.setAutoCommit(false);

        // Готовим запрос: списать 200 рублей у Ивана
        PreparedStatement pstmt1 = conn1.prepareStatement(
                "UPDATE bank_accounts SET balance = balance - 200 WHERE id = 1"
        );
        pstmt1.executeUpdate(); // Выполняем списание
        System.out.println("Транзакция 1: списала 200 рублей, но пока НЕ сохранила (не сделала COMMIT)");

        // 2. ВТОРОЙ ПОЛЬЗОВАТЕЛЬ (Транзакция 2)
        // Второй пользователь заходит в базу (новое соединение)
        Connection conn2 = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        conn2.setAutoCommit(false);

        // Второй пользователь тоже хочет списать 300 рублей у Ивана
        PreparedStatement pstmt2 = conn2.prepareStatement(
                "UPDATE bank_accounts SET balance = balance - 300 WHERE id = 1"
        );

        // ВАЖНЫЙ МОМЕНТ: Второй пользователь пытается обновить ту же строку.
        // На уровне READ COMMITTED (по умолчанию) он просто "зависнет" и будет ждать,
        // пока первый пользователь не сделает COMMIT или ROLLBACK.
        System.out.println("Транзакция 2: пытается списать 300 рублей и ждет...");

        // Мы запускаем второй поток, чтобы он обновил данные, когда первый освободит строку
        Thread updateThread = new Thread(() -> {
            try {
                pstmt2.executeUpdate();
                conn2.commit(); // Сохраняем изменения
                System.out.println("Транзакция 2: дождалась и списала 300 рублей");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        updateThread.start(); // Запускаем второй поток

        // Даем второму потоку время "зависнуть" в ожидании
        Thread.sleep(1000);

        // 3. ПЕРВЫЙ ПОЛЬЗОВАТЕЛЬ СОХРАНЯЕТ ИЗМЕНЕНИЯ
        conn1.commit();
        System.out.println("Транзакция 1: сделала COMMIT. Блокировка снята!");

        // Ждем, пока второй поток завершит работу
        updateThread.join();

        // 4. ПРОВЕРКА РЕЗУЛЬТАТА
        // Заходим в базу и смотрим, какой баланс получился
        Connection connCheck = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        ResultSet rs = connCheck.createStatement().executeQuery("SELECT balance FROM bank_accounts WHERE id = 1");
        rs.next();
        double finalBalance = rs.getDouble("balance");

        // Ожидаем: 1000 - 200 - 300 = 500
        System.out.println("Итоговый баланс: " + finalBalance);

        // Закрываем соединения
        conn1.close();
        conn2.close();
        connCheck.close();
    }
}