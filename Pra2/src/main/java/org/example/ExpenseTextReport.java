package org.example;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;

public class ExpenseTextReport {
    public String generate(ExpenseAnalysis analysis, LocalDate start, LocalDate end, double symbolValue) {
        if (!Double.isFinite(symbolValue) || symbolValue <= 0) {
            throw new IllegalArgumentException("Вартість символу має бути скінченним додатним числом");
        }
        StringBuilder report = new StringBuilder("Звіт про витрати за " + start + " — " + end + "\n");
        report.append(String.format(Locale.ROOT,
                "* = %.2f грн; + = залишок менший за один символ\n", symbolValue));
        report.append("Найбільша витрата: ").append(format(analysis.findLargestExpense(start, end))).append('\n');
        report.append("Найменша витрата: ").append(format(analysis.findSmallestExpense(start, end))).append('\n');
        appendTotals(report, "За категоріями (описами)", analysis.totalsByCategory(start, end), symbolValue);
        appendTotals(report, "За місяцями", analysis.totalsByMonth(start, end), symbolValue);
        return report.toString();
    }

    private String format(java.util.Optional<Transaction> expense) {
        return expense.map(t -> String.format(Locale.ROOT, "%s — %s: %.2f грн",
                t.getDate(), t.getDescription(), -t.getAmount())).orElse("витрат немає");
    }

    private void appendTotals(StringBuilder report, String title, Map<?, Double> totals, double scale) {
        report.append(title).append(":\n");
        if (totals.isEmpty()) report.append("Витрат немає\n");
        totals.forEach((key, total) -> {
            double count = Math.floor(total / scale);
            if (count > 10000) throw new IllegalArgumentException("Збільште вартість символу: рядок перевищує 10000 символів");
            report.append(String.format(Locale.ROOT, "%s: %.2f грн | %s%s\n", key, total,
                    "*".repeat((int) count), total % scale > 0 ? "+" : ""));
        });
    }
}
