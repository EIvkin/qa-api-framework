package tests;

import org.testng.annotations.Test;
import java.math.BigDecimal;
import java.math.RoundingMode;
import static org.testng.Assert.assertEquals;

public class MathTest {

    @Test
    public void testMoneyRounding() {
        // Представим, что цена товара 99.99, а скидка 10%
        BigDecimal price = new BigDecimal("99.99");
        BigDecimal discount = new BigDecimal("0.10");

        // Считаем сумму скидки: 99.99 * 0.10 = 9.999
        BigDecimal discountAmount = price.multiply(discount);

        // Ошибка джуна: просто округлить и получить 10.00
        // Правильно: использовать RoundingMode.HALF_UP (банковское округление)
        BigDecimal finalDiscount = discountAmount.setScale(2, RoundingMode.HALF_UP);

        // Проверяем, что скидка 10.00 (а не 9.99)
        assertEquals(finalDiscount.toString(), "10.00");

        // А теперь итоговая цена: 99.99 - 10.00 = 89.99
        BigDecimal finalPrice = price.subtract(finalDiscount);
        assertEquals(finalPrice.toString(), "89.99");

        System.out.println("Расчет прошел: итоговая цена " + finalPrice);
    }
}