package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Category electronics = new Category(1, "Електроніка");
        Category smartphones = new Category(2, "Смартфони");
        Category accessories = new Category(3, "Аксесуари");

        Product product1 = new Product(1, "Ноутбук", 19999.99, "Високопродуктивний ноутбук для роботи та ігор", electronics);
        Product product2 = new Product(2, "Смартфон", 12999.50, "Смартфон з великим екраном…", smartphones);
        Product product3 = new Product(3, "Навушники", 2499.00, "Бездротові навушники з шумозаглушенням", accessories);

        List<Order> orderHistory = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);
        Cart cart = new Cart();
        while (true) {
            System.out.println("\nВиберіть опцію:");
            System.out.println("1 - Переглянути список товарів");
            System.out.println("2 - Додати товар до кошика");
            System.out.println("3 - Переглянути кошик");
            System.out.println("4 - Видалити товар з кошику");
            System.out.println("5 - Зробити замовлення");
            System.out.println("6 - Історія замовлень");
            System.out.println("7 - Пошук товарів за назвою або категорією");
            System.out.println("0 - Вийти");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    System.out.println(product1);
                    System.out.println(product2);
                    System.out.println(product3);
                    break;

                case 2:
                    System.out.println("Введіть ID товару для додавання до кошика:");
                    int id = scanner.nextInt();

                    if (id == 1) cart.addProduct(product1);
                    else if (id == 2) cart.addProduct(product2);
                    else if (id == 3) cart.addProduct(product3);
                    else System.out.println("Товар з таким ID не знайдено");
                    break;

                case 3:
                    System.out.println(cart);
                    break;

                case 4:
                    if (cart.getProducts().isEmpty()) {
                        System.out.println("Кошик порожній. Немає чого видаляти.");
                        break;
                    }

                    System.out.println("Введіть ID товару для видалення з кошика:");
                    int removeId = scanner.nextInt();

                    Product toRemove = null;
                    if (removeId == 1) toRemove = product1;
                    else if (removeId == 2) toRemove = product2;
                    else if (removeId == 3) toRemove = product3;
                    else System.out.println("Товар з таким ID не існує.");

                    if (toRemove != null) {
                        if (cart.getProducts().contains(toRemove)) {
                            cart.removeProduct(toRemove);
                            System.out.println("Товар успішно видалено з кошика!");
                        } else {
                            System.out.println("Цього товару немає у вашому кошику.");
                        }
                    }
                    break;

                case 5:
                    if (cart.getProducts().isEmpty()) {
                        System.out.println("Кошик порожній. Додайте товари перед оформленням замовлення.");
                    } else {
                        Order order = new Order(cart);
                        orderHistory.add(order);
                        System.out.println("Замовлення оформлено:");
                        System.out.println(order);
                        cart.clear();
                    }
                    break;

                case 6:
                    System.out.println("\n--- Історія замовлень ---");
                    if (orderHistory.isEmpty()) {
                        System.out.println("Історія замовлень порожня.");
                    } else {
                        for (int i = 0; i < orderHistory.size(); i++) {
                            System.out.println("\nНомер замовлення #" + (i + 1));
                            System.out.println(orderHistory.get(i));
                        }
                    }
                    break;

                case 7:
                    scanner.nextLine();
                    System.out.println("Введіть назву товару або категорію для пошуку:");
                    String query = scanner.nextLine().toLowerCase();

                    boolean found = false;
                    Product[] allProducts = {product1, product2, product3};

                    for (Product product : allProducts) {
                        boolean matchName = product.getName().toLowerCase().contains(query);
                        boolean matchCategory = product.getCategory().getName().toLowerCase().contains(query);

                        if (matchName || matchCategory) {
                            System.out.println(product);
                            found = true;
                        }
                    }

                    if (!found) {
                        System.out.println("Товарів за запитом \"" + query + "\" не знайдено.");
                    }
                    break;

                case 0:
                    System.out.println("Дякуємо, що використовували наш магазин!");
                    return;

                default:
                    System.out.println("Невідома опція. Спробуйте ще раз.");
                    break;
            }
        }
    }
}