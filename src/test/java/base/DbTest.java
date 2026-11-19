package base;

import org.testng.annotations.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import static org.testng.Assert.assertEquals;

public class DbTest {

    @Test
    public void testDatabaseConnection() throws Exception {
        // Подключаемся к нашему локальному PostgreSQL
        String url = "jdbc:postgresql://localhost:5432/mydb";
        String user = "postgres";
        String password = "mysecretpassword";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1 AS result")) {

            rs.next();
            int result = rs.getInt("result");
            assertEquals(result, 1);
            System.out.println("Подключение к БД успешно! Запрос выполнен.");
        }
    }
}