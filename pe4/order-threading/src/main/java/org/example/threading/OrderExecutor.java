package org.example.threading;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.example.model.Order;
import org.example.model.Product;

public final class OrderExecutor implements AutoCloseable {
    private final ExecutorService executor;
    public OrderExecutor(int threadCount) {
        if (threadCount <= 0) throw new IllegalArgumentException("Кількість потоків має бути додатною");
        AtomicInteger counter = new AtomicInteger();
        executor = Executors.newFixedThreadPool(threadCount,
                task -> new Thread(task, "обробник-замовлень-" + counter.incrementAndGet()));
    }
    public <T extends Product> Future<Order<T>> submit(
            Order<T> order, Function<Order<T>, Order<T>> processor) {
        Objects.requireNonNull(order, "Потрібно вказати замовлення");
        Objects.requireNonNull(processor, "Потрібно вказати обробник");
        return executor.submit(() -> processor.apply(order));
    }
    @Override
    public void close() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(5, TimeUnit.SECONDS))
                    throw new IllegalStateException("Робочі потоки не завершилися");
            }
        } catch (InterruptedException exception) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
