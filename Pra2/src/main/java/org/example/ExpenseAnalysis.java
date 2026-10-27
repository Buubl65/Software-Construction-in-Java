package org.example;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ExpenseAnalysis {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd-MM-uuuu")
            .withResolverStyle(java.time.format.ResolverStyle.STRICT);
    private final List<Transaction> transactions;

    public ExpenseAnalysis(List<Transaction> transactions) {
        this.transactions = List.copyOf(transactions);
    }

    public List<Transaction> expensesBetween(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Початок періоду має бути не пізніше кінця");
        }
        return transactions.stream().filter(t -> t.getAmount() < 0)
                .filter(t -> {
                    LocalDate date = LocalDate.parse(t.getDate(), DATE);
                    return !date.isBefore(start) && !date.isAfter(end);
                }).collect(Collectors.toList());
    }

    public Optional<Transaction> findLargestExpense(LocalDate start, LocalDate end) {
        return expensesBetween(start, end).stream().min(Comparator.comparingDouble(Transaction::getAmount));
    }

    public Optional<Transaction> findSmallestExpense(LocalDate start, LocalDate end) {
        return expensesBetween(start, end).stream().max(Comparator.comparingDouble(Transaction::getAmount));
    }

    public Map<String, Double> totalsByCategory(LocalDate start, LocalDate end) {
        return expensesBetween(start, end).stream().collect(Collectors.groupingBy(
                Transaction::getDescription, TreeMap::new, Collectors.summingDouble(t -> -t.getAmount())));
    }

    public Map<YearMonth, Double> totalsByMonth(LocalDate start, LocalDate end) {
        return expensesBetween(start, end).stream().collect(Collectors.groupingBy(
                t -> YearMonth.from(LocalDate.parse(t.getDate(), DATE)),
                TreeMap::new, Collectors.summingDouble(t -> -t.getAmount())));
    }
}
