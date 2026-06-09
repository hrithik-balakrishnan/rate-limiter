package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Tell Spring Boot exactly where to find your controllers and services
@SpringBootApplication(scanBasePackages = {"org.example", "com.ratelimiter"})
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}