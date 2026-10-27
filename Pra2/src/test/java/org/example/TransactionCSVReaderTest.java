package org.example;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.Assert.*;

public class TransactionCSVReaderTest {
    @Test
    public void readsCsvFileWithUkrainianText() throws Exception {
        Path csv = Files.createTempFile("transactions-", ".csv");
        try {
            Files.writeString(csv, "01-01-2024,-1200.50,Продукти\n02-01-2024,5000,Зарплата\n",
                    StandardCharsets.UTF_8);
            List<Transaction> result = new TransactionCSVReader()
                    .parse(Files.readAllLines(csv, StandardCharsets.UTF_8));
            assertEquals(2, result.size());
            assertEquals("01-01-2024", result.get(0).getDate());
            assertEquals(-1200.50, result.get(0).getAmount(), 0.001);
            assertEquals("Продукти", result.get(0).getDescription());
            assertEquals(5000, result.get(1).getAmount(), 0.001);
            assertEquals("Зарплата", result.get(1).getDescription());
        } finally {
            Files.deleteIfExists(csv);
        }
    }

    @Test
    public void skipsIncorrectColumnCountsAndPreservesEmptyDescription() {
        List<Transaction> result = new TransactionCSVReader().parse(List.of(
                "invalid", "01-01-2024,-50,", "01-01-2024,-10,Food,extra"));
        assertEquals(1, result.size());
        assertEquals("", result.get(0).getDescription());
        assertTrue(new TransactionCSVReader().parse(List.of()).isEmpty());
    }
}
