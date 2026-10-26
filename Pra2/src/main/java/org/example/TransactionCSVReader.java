package org.example;

import java.util.ArrayList;
import java.util.List;

public class TransactionCSVReader {

    public List<Transaction> parse(List<String> lines) {
        List<Transaction> transactions = new ArrayList<>();

        for (String line : lines) {
            String[] values = line.split(",", -1);

            if (values.length != 3) {
                continue;
            }

            Transaction transaction = new Transaction(
                    values[0],
                    Double.parseDouble(values[1]),
                    values[2]
            );

            transactions.add(transaction);
        }

        return transactions;
    }
}