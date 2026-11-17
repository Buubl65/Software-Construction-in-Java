package org.example.storage;

import java.util.List;
import org.example.model.Order;
import org.example.model.Product;
import org.example.storage.internal.ConcurrentOrderStore;

public interface OrderStore<T extends Product> {
    /** Атомарно зберігає замовлення; false означає, що ідентифікатор уже існує. */
    boolean save(Order<T> order);
    List<Order<T>> findAll();
    static <T extends Product> OrderStore<T> inMemory() { return new ConcurrentOrderStore<>(); }
}
