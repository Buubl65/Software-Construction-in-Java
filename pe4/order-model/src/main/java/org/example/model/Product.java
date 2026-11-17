package org.example.model;

import java.math.BigDecimal;
import java.util.Objects;

public abstract class Product {
    private final String name;
    private final BigDecimal price;

    protected Product(String name, BigDecimal price) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Потрібно вказати назву товару");
        this.price = Objects.requireNonNull(price, "Потрібно вказати ціну");
        if (price.signum() <= 0) throw new IllegalArgumentException("Ціна має бути додатною");
        this.name = name;
    }
    public final String getName() { return name; }
    public final BigDecimal getPrice() { return price; }
}
