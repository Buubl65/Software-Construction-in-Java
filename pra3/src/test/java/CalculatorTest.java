import org.example.Calculator;
import org.example.InvalidInputException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CalculatorTest {

    Calculator calculator = new Calculator();

    @Test
    void testAddition() throws InvalidInputException {
        assertEquals(15.0, calculator.add(10, 5));
    }

    @Test
    void testSubtraction() throws InvalidInputException {
        assertEquals(5.0, calculator.subtract(10, 5));
    }

    @Test
    void testMultiplication() throws InvalidInputException {
        assertEquals(50.0, calculator.multiply(10, 5));
    }

    @Test
    void testDivision() throws InvalidInputException {
        assertEquals(2.0, calculator.divide(10, 5));
    }

    @Test
    void testDivisionByZero() {
        assertThrows(ArithmeticException.class,
                () -> calculator.divide(10, 0));
    }

    @Test
    void testInvalidInput() {
        assertThrows(InvalidInputException.class,
                () -> calculator.add(Double.NaN, 5));
    }
}
