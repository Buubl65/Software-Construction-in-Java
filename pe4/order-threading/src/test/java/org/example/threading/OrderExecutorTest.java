package org.example.threading;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import org.example.model.*;
import org.example.processing.*;
import org.example.storage.OrderStore;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderExecutorTest {
    @Test
    void duplicateSubmissionsHaveExactlyOneWinner() throws Exception {
        OrderStore<Electronics> store = OrderStore.inMemory();
        OrderProcessor<Electronics> processor = new OrderProcessor<>(store);
        Order<Electronics> order = new Order<>(UUID.randomUUID(), Electronics.builder()
                .name("Телефон").price(BigDecimal.TEN).warrantyMonths(12).build(), 1);
        List<Future<Order<Electronics>>> futures = new ArrayList<>();
        int successes = 0;
        try (OrderExecutor executor = new OrderExecutor(4)) {
            for (int i = 0; i < 100; i++) futures.add(executor.submit(order, processor::process));
            for (Future<Order<Electronics>> future : futures) {
                try {
                    future.get(5, TimeUnit.SECONDS);
                    successes++;
                } catch (ExecutionException exception) {
                    assertInstanceOf(OrderProcessingException.class, exception.getCause());
                }
            }
        }
        assertEquals(1, successes);
        assertEquals(1, store.findAll().size());
    }
    @Test
    void workersExecuteConcurrentlyAndRejectWorkAfterClose() throws Exception {
        Order<Electronics> order = new Order<>(UUID.randomUUID(), Electronics.builder()
                .name("Телефон").price(BigDecimal.TEN).warrantyMonths(12).build(), 1);
        CyclicBarrier barrier = new CyclicBarrier(2);
        Set<String> threads = ConcurrentHashMap.newKeySet();
        OrderExecutor executor = new OrderExecutor(2);
        try (executor) {
            java.util.function.Function<Order<Electronics>, Order<Electronics>> task = value -> {
                threads.add(Thread.currentThread().getName());
                try { barrier.await(5, TimeUnit.SECONDS); }
                catch (Exception exception) { throw new IllegalStateException(exception); }
                return value;
            };
            var first = executor.submit(order, task);
            var second = executor.submit(order, task);
            first.get(10, TimeUnit.SECONDS);
            second.get(10, TimeUnit.SECONDS);
        }
        assertEquals(2, threads.size());
        assertThrows(RejectedExecutionException.class, () -> executor.submit(order, value -> value));
    }
}
