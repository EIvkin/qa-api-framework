package tests;

import org.testng.annotations.Test;

import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class DateBoundaryTest {

    private static final Logger log = LoggerFactory.getLogger(DateBoundaryTest.class);

    // Бизнес-логика: скидка действует с 1 по 10 ноября включительно
    private boolean isDiscountActive(LocalDate date) {
        LocalDate start = LocalDate.of(2026, 11, 1);
        LocalDate end = LocalDate.of(2026, 11, 10);
        // isBefore или isEqual (включительно)
        return !date.isBefore(start) && !date.isAfter(end);
    }

    @Test
    public void testDiscountBoundaries() {
        // Проверяем границы и чуть за ними
        assertFalse(isDiscountActive(LocalDate.of(2026, 10, 31)), "31 октября - скидки нет");
        assertTrue(isDiscountActive(LocalDate.of(2026, 11, 1)), "1 ноября - скидка есть (начало)");
        assertTrue(isDiscountActive(LocalDate.of(2026, 11, 10)), "10 ноября - скидка есть (конец)");
        assertFalse(isDiscountActive(LocalDate.of(2026, 11, 11)), "11 ноября - скидки нет");

        System.out.println("Границы периода проверены!");
        log.info("Границы периода проверены!");
    }
}