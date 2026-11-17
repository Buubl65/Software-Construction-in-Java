package org.example.app;

import com.github.javafaker.Faker;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.example.model.*;
import org.example.processing.OrderProcessor;
import org.example.storage.OrderStore;
import org.example.threading.OrderExecutor;

public final class Main {
    public static void main(String[] args) {
        Faker faker = new Faker(Locale.forLanguageTag("uk"));
        OrderStore<Product> store = OrderStore.inMemory();
        OrderProcessor<Product> processor = new OrderProcessor<>(store);
        List<Order<Product>> orders = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            BigDecimal price = BigDecimal.valueOf(faker.number().numberBetween(1000, 100000), 2);
            Product product = i % 2 == 0
                    ? Electronics.builder().name(faker.options().option("Ноутбук", "Смартфон", "Навушники", "Планшет", "Монітор")).price(price)
                        .warrantyMonths(faker.number().numberBetween(1, 37)).build()
                    : Clothing.builder().name(faker.options().option("Сорочка", "Куртка", "Штани", "Светр", "Футболка")).price(price)
                        .size(faker.options().option("S", "M", "L", "XL")).build();
            orders.add(new Order<>(UUID.randomUUID(), product, faker.number().numberBetween(1, 5)));
        }
        long clothingCount = orders.stream().filter(order -> order.product() instanceof Clothing).count();
        System.out.println("Замовлень одягу: " + clothingCount);
        List<Future<Order<Product>>> results = new ArrayList<>();
        try (OrderExecutor executor = new OrderExecutor(4)) {
            orders.forEach(order -> results.add(executor.submit(order, processor::process)));
            // Демонструємо окрему помилку без перезапису наявного замовлення.
            results.add(executor.submit(orders.get(0), processor::process));
            int failures = 0;
            for (Future<Order<Product>> result : results) {
                try {
                    result.get();
                } catch (ExecutionException exception) {
                    failures++;
                    System.out.println("Помилка замовлення: " + exception.getCause().getMessage());
                }
            }
            System.out.printf("Збережено: %d, помилок: %d%n", store.findAll().size(), failures);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            System.err.println("Обробку замовлень перервано");
        }
    }
}
