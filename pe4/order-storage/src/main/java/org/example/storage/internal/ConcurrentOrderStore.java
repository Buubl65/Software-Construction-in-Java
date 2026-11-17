package org.example.storage.internal;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.example.model.Order;
import org.example.model.Product;
import org.example.storage.OrderStore;

// Модуль навмисно не експортує цей пакет.
public final class ConcurrentOrderStore<T extends Product> implements OrderStore<T> {
    private final ConcurrentHashMap<UUID, Order<T>> orders = new ConcurrentHashMap<>();
    @Override
    public boolean save(Order<T> order) { return orders.putIfAbsent(order.id(), order) == null; }
    @Override
    public List<Order<T>> findAll() { return List.copyOf(orders.values()); }
}
