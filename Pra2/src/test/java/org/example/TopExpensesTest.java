package org.example;

import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class TopExpensesTest {
    @Test
    public void returnsTenLargestExpensesInDescendingMagnitude() {
        List<Transaction> transactions = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            transactions.add(new Transaction("01-01-2024", -i * 100, "Витрата " + i));
        }
        transactions.add(new Transaction("01-01-2024", 100000, "Дохід"));
        transactions.add(new Transaction("01-01-2024", 0, "Нуль"));
        List<Transaction> result = new TransactionAnalyzer(transactions).findTopExpenses();
        assertEquals(10, result.size());
        for (int i = 0; i < 10; i++) {
            assertEquals(-(12 - i) * 100, result.get(i).getAmount(), 0.001);
        }
        assertEquals(14, transactions.size());
    }

    @Test
    public void handlesFewerThanTenExpensesAndNoExpenses() {
        Transaction expense = new Transaction("01-01-2024", -10, "Витрата");
        assertEquals(List.of(expense), new TransactionAnalyzer(List.of(expense,
                new Transaction("01-01-2024", 20, "Дохід"))).findTopExpenses());
        assertTrue(new TransactionAnalyzer(List.of()).findTopExpenses().isEmpty());
        assertTrue(new TransactionAnalyzer(List.of(new Transaction("01-01-2024", 20, "Дохід")))
                .findTopExpenses().isEmpty());
    }
}
