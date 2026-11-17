package org.example.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public record Order<T extends Product>(UUID id, T product, int quantity) {
    public Order {
        Objects.requireNonNull(id, "Потрібно вказати ідентифікатор");
        Objects.requireNonNull(product, "Потрібно вказати товар");
        if (quantity <= 0) throw new IllegalArgumentException("Кількість має бути додатною");
    }
    public BigDecimal total() { return product.getPrice().multiply(BigDecimal.valueOf(quantity)); }
}
