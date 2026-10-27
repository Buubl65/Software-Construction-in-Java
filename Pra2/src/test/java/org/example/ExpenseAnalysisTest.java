package org.example;

import org.junit.Test;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import static org.junit.Assert.*;

public class ExpenseAnalysisTest {
    private final LocalDate start = LocalDate.of(2024, 1, 1);
    private final LocalDate end = LocalDate.of(2024, 2, 29);
    private final ExpenseAnalysis analysis = new ExpenseAnalysis(List.of(
            new Transaction("31-12-2023", -9000, "Поза періодом"),
            new Transaction("01-01-2024", -2500, "Продукти"),
            new Transaction("15-01-2024", -500, "Продукти"),
            new Transaction("29-02-2024", -1000, "Транспорт"),
            new Transaction("01-03-2024", -1, "Поза періодом"),
            new Transaction("15-01-2024", 50000, "Дохід"),
            new Transaction("15-01-2024", 0, "Нуль")));

    @Test
    public void findsExtremesIncludingBothPeriodBoundaries() {
        assertEquals(-2500, analysis.findLargestExpense(start, end).orElseThrow().getAmount(), 0.001);
        assertEquals(-500, analysis.findSmallestExpense(start, end).orElseThrow().getAmount(), 0.001);
        assertEquals(1, analysis.expensesBetween(start, start).size());
        assertFalse(analysis.findLargestExpense(end.plusDays(2), end.plusDays(3)).isPresent());
        assertFalse(analysis.findSmallestExpense(end.plusDays(2), end.plusDays(3)).isPresent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsReversedPeriod() {
        analysis.expensesBetween(end, start);
    }

    @Test
    public void sumsOnlyExpensesByCategoryAndMonth() {
        assertEquals(2, analysis.totalsByCategory(start, end).size());
        assertEquals(3000, analysis.totalsByCategory(start, end).get("Продукти"), 0.001);
        assertEquals(1000, analysis.totalsByCategory(start, end).get("Транспорт"), 0.001);
        assertEquals(3000, analysis.totalsByMonth(start, end).get(YearMonth.of(2024, 1)), 0.001);
        assertEquals(1000, analysis.totalsByMonth(start, end).get(YearMonth.of(2024, 2)), 0.001);
    }

    @Test
    public void reportShowsTotalsBarsAndRemainders() {
        String report = new ExpenseTextReport().generate(analysis, start, end, 1000);
        assertTrue(report.contains("Продукти: 3000.00 грн | ***\n"));
        assertTrue(report.contains("2024-01: 3000.00 грн | ***\n"));
        assertTrue(report.contains("2024-02: 1000.00 грн | *\n"));
        ExpenseAnalysis partial = new ExpenseAnalysis(List.of(new Transaction("01-01-2024", -500, "Їжа")));
        assertTrue(new ExpenseTextReport().generate(partial, start, end, 1000).contains("Їжа: 500.00 грн | +\n"));
        assertTrue(new ExpenseTextReport().generate(new ExpenseAnalysis(List.of()), start, end, 1000)
                .contains("витрат немає"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsZeroScale() {
        new ExpenseTextReport().generate(analysis, start, end, 0);
    }
}
