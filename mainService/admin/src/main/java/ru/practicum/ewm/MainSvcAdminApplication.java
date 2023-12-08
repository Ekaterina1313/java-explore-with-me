package ru.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "ru.practicum.ewm.repository")
@EntityScan(basePackages = {"ru.practicum.common.model"})
public class MainSvcAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainSvcAdminApplication.class, args);
    }
}