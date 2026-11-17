# Багатопотокова система замовлень

Навчальний Java-проєкт із JPMS, обмеженими узагальненнями, Lombok та JavaFaker.
Потрібен JDK 27 і налаштований `JAVA_HOME`. Gradle використовує JDK 27 через Java toolchain;
цільова версія байткоду — Java 17. В IntelliJ IDEA виберіть JDK 27 для Project SDK і Gradle JVM.
Lombok 1.18.48 підтримує компіляцію на JDK 27.

Для запуску кнопкою ▶ відкрийте `Main.java` і виберіть `Run Main.main()`.
Після зміни налаштувань проєкту натисніть `Reload All Gradle Projects` у панелі Gradle.
Налаштування модульного шляху застосовуються і до звичайної задачі `run`, і до задач,
які IntelliJ створює для запуску `main`.

```powershell
.\gradlew.bat build
.\gradlew.bat run
.\gradlew.bat test
```

На Linux/macOS використовуйте `./gradlew`. Перше збирання потребує доступу до Maven Central.

## Модулі

| Модуль | Відповідальність |
| --- | --- |
| `order.model` | Незмінні `Product`, `Clothing`, `Electronics`, `Order<T extends Product>` |
| `order.storage` | `OrderStore<T extends Product>` і потокобезпечне сховище в пам'яті |
| `order.processing` | `OrderProcessor<T extends Product>` і `OrderProcessingException` |
| `order.threading` | Пул потоків `OrderExecutor`, задачі й `Future` |
| `order.app` | Генерація даних JavaFaker і демонстрація |

`requires` задає залежності, `requires transitive` надає типи публічного API клієнтам.
`exports` відкриває лише API-пакети. `org.example.storage.internal` не експортується:
інші модулі створюють сховище через `OrderStore.inMemory()`, а не через його реалізацію.
Демонстрація запускається на module path, тому обмеження JPMS діють під час виконання.
Lombok потрібен лише при компіляції (`requires static lombok` та annotation processor).
JavaFaker і його залежності розміщуються на module path як автоматичні модулі.

## Поведінка

`@Builder` на конструкторах `Clothing` і `Electronics` включає успадковані дані товару
та перевіряє назву, ціну, розмір і гарантію. Ціни представлені `BigDecimal`.
JavaFaker генерує назви, ціни, кількості та характеристики десяти товарів.
Лямбда фільтрує одяг; `processor::process` передає обробник у пул із чотирьох потоків.

`ConcurrentHashMap.putIfAbsent` атомарно відхиляє повторний UUID навіть за одночасної
обробки. Результати повертаються через `Future`; помилка одного замовлення не зупиняє
решту. Виняток зберігає ID замовлення та початкову причину. Java не дозволяє
узагальнені підкласи `Throwable`, тому сам клас винятку не має параметра типу.
`try-with-resources` завершує пул; переривання зберігає interrupt-прапорець.
Сховище повертає незмінну копію списку, дані існують лише до завершення процесу.

Демонстрація навмисно надсилає одне замовлення повторно: очікуваний підсумок
`Збережено: 10, помилок: 1`. Порядок рядків залежить від планувальника потоків.

Тести перевіряють валідацію, розрахунок вартості, причини винятків, незмінність
списку, атомарність 100 повторних подань, паралельне виконання та закриття пулу.

Залежності: [Lombok](https://projectlombok.org/setup/javac),
[JavaFaker](https://github.com/DiUS/java-faker).
