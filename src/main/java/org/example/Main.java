package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Main {

    public static void main(String[] args) {
        // This spins up the embedded Tomcat web server engine on port 8080
        SpringApplication.run(Main.class, args);
    }

    // This creates your very first live, network-accessible API endpoint
    @GetMapping("/health")
    public String checkHealth() {
        return "{\"status\": \"UP\", \"service\": \"rate-limiter-engine\"}";
    }
}