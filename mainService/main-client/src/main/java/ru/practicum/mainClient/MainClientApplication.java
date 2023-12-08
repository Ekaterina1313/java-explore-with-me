package ru.practicum.mainClient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"ru.practicum.common.model"})
public class MainClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainClientApplication.class, args);
    }
}