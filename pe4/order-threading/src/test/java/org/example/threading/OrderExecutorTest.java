package org.example.threading;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import org.example.model.Electronics;
import org.example.model.Order;
import org.example.processing.OrderProcessor;
import org.example.storage.OrderStore;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderExecutorTest {
    private Order<Electronics> order() {
        Electronics product = Electronics.builder()
                .name("Телефон").price(BigDecimal.TEN).warrantyMonths(12).build();
        return new Order<>(UUID.randomUUID(), product, 1);
    }

    @Test
    void duplicateOrdersAreStoredOnce() {
        OrderStore<Electronics> store = OrderStore.inMemory();
        OrderProcessor<Electronics> processor = new OrderProcessor<>(store);
        OrderExecutor executor = new OrderExecutor(4);

        executor.process(Collections.nCopies(20, order()), processor::process);

        assertEquals(1, store.findAll().size());
    }

    @Test
    void processesOrdersInParallelAndWaitsForCompletion() {
        CyclicBarrier barrier = new CyclicBarrier(2);
        Set<String> threads = ConcurrentHashMap.newKeySet();
        Set<UUID> completed = ConcurrentHashMap.newKeySet();
        List<Order<Electronics>> orders = List.of(order(), order());

        new OrderExecutor(2).process(orders, order -> {
            threads.add(Thread.currentThread().getName());
            try {
                barrier.await(5, TimeUnit.SECONDS);
            } catch (Exception exception) {
                throw new IllegalStateException(exception);
            }
            completed.add(order.id());
        });

        assertEquals(2, threads.size());
        assertEquals(2, completed.size());
    }
}
