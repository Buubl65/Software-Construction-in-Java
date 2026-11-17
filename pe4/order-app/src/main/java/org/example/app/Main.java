package org.example.app;

import com.github.javafaker.Faker;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.example.model.Clothing;
import org.example.model.Electronics;
import org.example.model.Order;
import org.example.model.Product;
import org.example.processing.OrderProcessor;
import org.example.storage.OrderStore;
import org.example.threading.OrderExecutor;

public final class Main {
    public static void main(String[] args) {
        Faker faker = new Faker(Locale.forLanguageTag("uk"));
        OrderStore<Product> store = OrderStore.inMemory();
        OrderProcessor<Product> processor = new OrderProcessor<>(store);
        List<Order<Product>> orders = new ArrayList<>();

        // Створюємо по п’ять товарів кожного типу через Lombok Builder.
        for (int i = 0; i < 5; i++) {
            Electronics electronics = Electronics.builder()
                    .name(faker.options().option("Ноутбук", "Смартфон", "Навушники"))
                    .price(BigDecimal.valueOf(faker.number().numberBetween(1000, 100000), 2))
                    .warrantyMonths(faker.number().numberBetween(1, 37))
                    .build();
            Clothing clothing = Clothing.builder()
                    .name(faker.options().option("Сорочка", "Куртка", "Штани"))
                    .price(BigDecimal.valueOf(faker.number().numberBetween(1000, 100000), 2))
                    .size(faker.options().option("S", "M", "L", "XL"))
                    .build();
            orders.add(new Order<>(UUID.randomUUID(), electronics, faker.number().numberBetween(1, 5)));
            orders.add(new Order<>(UUID.randomUUID(), clothing, faker.number().numberBetween(1, 5)));
        }

        // Лямбда відбирає замовлення одягу.
        long clothingCount = orders.stream()
                .filter(order -> order.product() instanceof Clothing)
                .count();
        System.out.println("Замовлень одягу: " + clothingCount);

        // Повторне замовлення показує обробку винятку.
        orders.add(orders.get(0));
        OrderExecutor executor = new OrderExecutor(4);
        executor.process(orders, processor::process);

        int saved = store.findAll().size();
        System.out.printf("Збережено: %d, помилок: %d%n", saved, orders.size() - saved);
    }
}
