package org.example.model;

import java.math.BigDecimal;
import lombok.Builder;

public final class Electronics extends Product {
    private final int warrantyMonths;
    @Builder
    private Electronics(String name, BigDecimal price, int warrantyMonths) {
        super(name, price);
        if (warrantyMonths <= 0) throw new IllegalArgumentException("Гарантійний термін має бути додатним");
        this.warrantyMonths = warrantyMonths;
    }
    public int getWarrantyMonths() { return warrantyMonths; }
}
