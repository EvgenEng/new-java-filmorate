package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FilmorateApplication {
    static {
        try {
            Class.forName("org.h2.Driver");
            System.out.println("H2 Driver loaded successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("H2 Driver loading failed!");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(FilmorateApplication.class, args);
    }
}
