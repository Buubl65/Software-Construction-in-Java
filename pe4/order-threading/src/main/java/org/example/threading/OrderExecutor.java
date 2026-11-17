package org.example.threading;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import org.example.model.Order;
import org.example.model.Product;

public final class OrderExecutor {
    private final int threadCount;

    public OrderExecutor(int threadCount) {
        if (threadCount <= 0) {
            throw new IllegalArgumentException("Кількість потоків має бути додатною");
        }
        this.threadCount = threadCount;
    }

    public <T extends Product> void process(List<Order<T>> orders, Consumer<Order<T>> processor) {
        // Пул виконує задачі паралельно. Закриття чекає завершення всіх задач.
        try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
            for (Order<T> order : orders) {
                executor.submit(() -> {
                    try {
                        processor.accept(order);
                    } catch (RuntimeException exception) {
                        System.out.println("Помилка замовлення: " + exception.getMessage());
                    }
                });
            }
        }
    }
}
