package org.example.processing;

import java.math.BigDecimal;
import java.util.UUID;
import org.example.model.Clothing;
import org.example.model.Order;
import org.example.storage.OrderStore;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderProcessorTest {
    private Order<Clothing> order() {
        return new Order<>(UUID.randomUUID(), Clothing.builder()
                .name("Сорочка").price(new BigDecimal("12.50")).size("M").build(), 3);
    }
    @Test
    void storesOrderAndRejectsDuplicateWithContext() {
        OrderStore<Clothing> store = OrderStore.inMemory();
        OrderProcessor<Clothing> processor = new OrderProcessor<>(store);
        Order<Clothing> order = order();
        assertSame(order, processor.process(order));
        assertEquals(new BigDecimal("37.50"), order.total());
        OrderProcessingException error = assertThrows(OrderProcessingException.class,
                () -> processor.process(order));
        assertEquals(order.id(), error.getOrderId());
        assertInstanceOf(IllegalStateException.class, error.getCause());
        assertEquals(1, store.findAll().size());
        assertThrows(UnsupportedOperationException.class, () -> store.findAll().clear());
    }
    @Test
    void validatesProductsAndOrders() {
        assertThrows(IllegalArgumentException.class, () -> Clothing.builder()
                .name("Сорочка").price(BigDecimal.ZERO).size("M").build());
        assertThrows(IllegalArgumentException.class, () -> new Order<>(UUID.randomUUID(), order().product(), 0));
    }
    @Test
    void wrapsStorageFailures() {
        OrderStore<Clothing> broken = new OrderStore<>() {
            public boolean save(Order<Clothing> order) { throw new IllegalStateException("Сховище недоступне"); }
            public java.util.List<Order<Clothing>> findAll() { return java.util.List.of(); }
        };
        assertThrows(OrderProcessingException.class, () -> new OrderProcessor<>(broken).process(order()));
    }
}
