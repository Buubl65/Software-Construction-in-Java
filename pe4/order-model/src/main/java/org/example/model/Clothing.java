package org.example.model;

import java.math.BigDecimal;
import lombok.Builder;

public final class Clothing extends Product {
    private final String size;
    @Builder
    private Clothing(String name, BigDecimal price, String size) {
        super(name, price);
        if (size == null || size.isBlank()) throw new IllegalArgumentException("Потрібно вказати розмір");
        this.size = size;
    }
    public String getSize() { return size; }
}
