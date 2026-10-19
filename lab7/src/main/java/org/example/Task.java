package org.example;
import java.lang.*;


class Task implements Runnable {
    public void run() {
        System.out.println("Запуск нового потоку");
    }
}

