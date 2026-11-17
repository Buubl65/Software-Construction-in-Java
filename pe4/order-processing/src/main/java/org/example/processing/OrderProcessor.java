package org.example.processing;

import java.util.Objects;
import org.example.model.Order;
import org.example.model.Product;
import org.example.storage.OrderStore;

public final class OrderProcessor<T extends Product> {
    private final OrderStore<T> store;
    public OrderProcessor(OrderStore<T> store) { this.store = Objects.requireNonNull(store, "Потрібно вказати сховище"); }
    public Order<T> process(Order<T> order) {
        Objects.requireNonNull(order, "Потрібно вказати замовлення");
        try {
            if (!store.save(order)) throw new IllegalStateException("Ідентифікатор замовлення вже існує");
        } catch (RuntimeException exception) {
            throw new OrderProcessingException(order.id(), "Не вдалося обробити замовлення " + order.id(), exception);
        }
        System.out.printf("[%s] %s × %d = %s%n", Thread.currentThread().getName(),
                order.product().getName(), order.quantity(), order.total());
        return order;
    }
}
