package org.example.processing;

import java.util.UUID;

// Java забороняє узагальнені підкласи Throwable, тому зберігаємо ідентифікатор замовлення.
public final class OrderProcessingException extends RuntimeException {
    private final UUID orderId;
    public OrderProcessingException(UUID orderId, String message, Throwable cause) {
        super(message, cause);
        this.orderId = orderId;
    }
    public UUID getOrderId() { return orderId; }
}
