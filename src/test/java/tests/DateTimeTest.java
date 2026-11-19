package tests;

import org.testng.annotations.Test;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import static org.testng.Assert.assertEquals;

public class DateTimeTest {

    @Test
    public void testTimezoneBoundaries() {
        // Представим, что мы тестируем период действия скидки до 2026-11-17 23:59:59
        // Время сервера (UTC)
        ZonedDateTime serverTime = ZonedDateTime.of(2026, 11, 17, 20, 59, 59, 0, ZoneId.of("UTC"));

        // Время в Москве (UTC+3)
        ZonedDateTime moscowTime = serverTime.withZoneSameInstant(ZoneId.of("Europe/Moscow"));

        // Проверяем, что в Москве это уже 23:59:59 (то есть скидка еще действует)
        assertEquals(moscowTime.getHour(), 23);

        // А теперь проверим границу: следующий день в UTC
        ZonedDateTime nextDayUtc = serverTime.plusDays(1);
        ZonedDateTime nextDayMoscow = nextDayUtc.withZoneSameInstant(ZoneId.of("Europe/Moscow"));

        // В Москве это уже 18 ноября 02:59:59 (скидка не действует!)
        assertEquals(nextDayMoscow.getDayOfMonth(), 18);

        System.out.println("Таймзоны проверены: UTC " + serverTime + " -> Москва " + moscowTime);
    }
}