package com.example.MySpringConsoleHelloWorld;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

@SpringBootApplication
public class MySpringConsoleHelloWorldApplication implements CommandLineRunner {

    public static void main(String[] args) {
        System.out.println("Begin of main");
        SpringApplication.run(MySpringConsoleHelloWorldApplication.class, args);
        System.out.println("End of main");
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Hello from Spring Boot!");
    }

    @Bean
    @Order(1)
    public CommandLineRunner firstRunner() {
        return args -> System.out.println("First");
    }

    @Bean
    @Order(2)
    public CommandLineRunner secondRunner() {
        return args -> System.out.println("Second");
    }
}