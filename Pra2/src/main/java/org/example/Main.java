package org.example;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String filePath = "https://informer.com.ua/dut/java/pr2.csv";

        DataReader reader = new URLDataReader();
        TransactionCSVReader csvReader = new TransactionCSVReader();

        try {
            List<String> lines = reader.read(filePath);
            List<Transaction> transactions = csvReader.parse(lines);

            TransactionAnalyzer analyzer = new TransactionAnalyzer(transactions);
            TransactionReportGenerator reportGenerator = new TransactionReportGenerator();

            double totalBalance = analyzer.calculateTotalBalance();
            reportGenerator.printBalanceReport(totalBalance);

            String monthYear = "01-2024";
            int transactionsCount = analyzer.countTransactionsByMonth(monthYear);
            reportGenerator.printTransactionsCountByMonth(monthYear, transactionsCount);

            List<Transaction> topExpenses = analyzer.findTopExpenses();
            reportGenerator.printTopExpensesReport(topExpenses);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}