package org.example;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

public class ExpenseReportMain {
    public static void main(String[] args) throws java.io.IOException {
        if (args.length < 4 || args.length > 5) {
            System.out.println("Аргументи: <CSV> <yyyy-MM-dd> <yyyy-MM-dd> <звіт.txt> [грн за символ, типово 1000]");
            return;
        }
        ExpenseAnalysis analysis = new ExpenseAnalysis(new TransactionCSVReader()
                .parse(Files.readAllLines(Path.of(args[0]), StandardCharsets.UTF_8)));
        String report = new ExpenseTextReport().generate(analysis, LocalDate.parse(args[1]),
                LocalDate.parse(args[2]), args.length == 5 ? Double.parseDouble(args[4]) : 1000);
        Files.writeString(Path.of(args[3]), report, StandardCharsets.UTF_8);
        System.out.print(report);
    }
}
